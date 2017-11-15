import React, { Component } from 'react';

import ReactDOM from 'react-dom';
import axios from 'axios';
import moment from 'moment';
import '../style/style.css'

var url = "http://localhost:9200/"
   
var t = {
   'application/vnd.openxmlformats-officedocument.wordprocessingml.document' : 'DOC'
}
var locale = window.navigator.userLanguage || window.navigator.language;
moment.locale(locale); // 'fr_FR'

class SearchApp extends React.Component {
   constructor(props) {
      super(props);
      this.state = {
            loading : false,
            loaded : true,
            queryRequest : { query: {}, from: 0, size: 10 },
            hits : { hits: [], total: 0 },
            took: 0
          };
   }
   handleSearch = (queryRequest, merge) => {
      console.log("handleSearch");
      if (merge) {
         queryRequest = {...this.state.queryRequest, ...queryRequest, query: {...this.state.queryRequest.query, ...queryRequest.query}}
      } else {
         queryRequest.from = 0;
         queryRequest.size = 10;
      }
      this.setState(prevState => ({...prevState, queryRequest: queryRequest }));
      axios.post(url + "files/file/_search", queryRequest).then(response => {
         this.setState(prevState => ({...prevState, hits: response.data.hits, took: response.data.took }));
         console.log("response state", this.state);
      }).catch(error => {
         console.log("error", error);
      });
   }
   handlePrevious = () => {
      var size = this.state.queryRequest.size;
      this.handleSearch({...this.state.queryRequest, from: this.state.queryRequest.from - size, size: size}, true);
   }
   handleNext = () => {
      var size = this.state.queryRequest.size;
      this.handleSearch({...this.state.queryRequest, from: this.state.queryRequest.from + size, size: size}, true);
   }
   render = () => {
      return (
            <div>
            
            <SearchBar handleSearch={this.handleSearch}/>
            
            Focus rather than browse / rollover browse mode (facets) / keep previous criteria
            
            { this.state.hits.total == 0 ? '' : (
            <div>
            
            <div>
            Results {this.state.queryRequest.from + 1}-{this.state.queryRequest.from + this.state.queryRequest.size} of {this.state.hits.total}
            &nbsp;
            for query {JSON.stringify(this.state.queryRequest.query, null, '\t')}.
            Search took {this.state.took}ms. Sort by&nbsp;
            <a title="sort by last modified date" onClick={() => this.handleSearch({from: 0, size: 10, sort: [{'file.last_modified': 'desc'}]}, true)}>date</a>
            &nbsp;/&nbsp;
            <a title="sort by score" onClick={() => this.handleSearch({from: 0, size: 10, sort: ['_score']}, true)}>relevance</a>
            .
            
            </div>

            {  // && this.state.currentPage != 1
               (this.state.queryRequest.from != 0) ? (
                     <div><span title="previous" onClick={() => this.handlePrevious()} style={{color:'blue'}}>&lt; ...</span></div>
               ) : ''
            }
            <ResultList hits={this.state.hits} handleSearch={this.handleSearch}/>
            {
               (this.state.queryRequest.from + this.state.queryRequest.size < this.state.hits.total - 1) ? (
                     <div><span title="next" onClick={() => this.handleNext()} style={{color:'blue'}}>... &gt;</span></div>
               ) : ''
            }

            </div>
            ) }
            
            </div>
      );
   }
}

class SearchBar extends React.Component {
   constructor(props) {
      super(props);
      this.state = {
            searchText : ""
      };
   }
   onSearchTextChange = (e) => {
      var searchText = e.target.value;
      this.setState((prevState) => ({...prevState, searchText:searchText })); // searchText:e.target.value KO !
      console.log("onSearchTextChange", this.state, e);
   }
   handleSearchText = () => {
      this.props.handleSearch({ query: { multi_match: { query: this.state.searchText, fields: [ "path.tree^0.5", "file.name", "meta.author", "meta.title", "fulltext^0.8" ] } },
         highlight: { fields: { fulltext: { type: 'unified' } } } });
   }
   render() {
      return (
            <div>
            <input onChange={(e) => this.onSearchTextChange(e)}/>
            &nbsp;
            <span onClick={this.handleSearchText}>search</span>
            &nbsp;- images - advanced - tips
            </div>
      );
   }
}

class ResultList extends React.Component {
   constructor(props) {
      super(props);
      this.state = {
            currentPage: 1
          };
   }
   displayFileDetails = () => {
      console.log("displayFileDetails");
   }
   handleBrowseToPath = (pathElts, depth) => {
      this.props.handleSearch({ query: { term: { 'path.tree': '/' + pathElts.slice(0, depth + 1).join('/') } } }); // file.path.tree '/AkIesvz+/home/mardut/dev/pcu/workshop_elastic/kibana-5.2.2-linux-x86_64/README.txt'
   }
   handleFindLater = (lastModified) => {
      this.props.handleSearch({ query: { range: { 'file.last_modified': { gt: lastModified } } } });
   }
   handleFindBigger = (length) => {
      this.props.handleSearch({ query: { range: { 'content.length': { gt: length } } } });
   }
   handleFindSameHash = (hash) => {
      this.props.handleSearch({ query: { term: { 'content.hash': hash } } });
   }
   render() {
      return (
            <div>
            {
               this.props.hits.hits.map(function(hit, hitInd) {
                  var path = hit._source.path;
                  var pathElts = path.substring(1).split('/'); // remove root / first
                  var hostPathElt = pathElts[0];
                  var filePathElt = pathElts[pathElts.length - 1];
                  var pathSpan = pathElts.slice(1, -1).map(function(pathElt, pathEltInd) { // remove then special cases (host & file)
                     return (
                        <span key={pathEltInd}>/<span title="browse to path" onClick={() => this.handleBrowseToPath(pathElts, pathEltInd + 1)} style={{color:'blue'}}>{pathElt}</span></span>
                     );
                  }.bind(this));
                  
                  var title = hit._source.meta.title ? hit._source.meta.title : filePathElt;
                  // or any other metas, including for dir : Index of x Name Last modified Size Parent [DIR]
                  
                  var lastModifiedMoment = moment(hit._source.file.last_modified); // "2011-04-11T10:20:30Z" "2016-10-01T15:29:45.000+0000"
                  
                  return (
                     <div key={hitInd}>
                     
                     <div>
                     [{t[hit._source.http.mimetype]}]
                     &nbsp;
                     <a href={hit._source.http.url} title={JSON.stringify(hit, null, '\t')}>{title}</a>
                     </div>
                     
                     <div>
                     { hit.highlight ? hit.highlight.fulltext.map(function(hl, hlInd) {
                        var res = [];
                        var emInd = 0;
                        var endEmInd;
                        while ((emInd = hl.indexOf('<em>', emInd)) != -1) {
                           res.push({ text: hl.substring(endEmInd, emInd) });
                           endEmInd = hl.indexOf('</em>', emInd)
                           res.push({ hl:true, text: hl.substring(emInd + 4, endEmInd) });
                           emInd = endEmInd + 5;
                           endEmInd = emInd;
                        }
                        if (endEmInd < hl.length - 1) { // highlight line did not end by a stressed part
                           res.push({ text: hl.substring(endEmInd, hl.length) });
                           res.push({ text: '... ' });
                        } else if (hlInd < hit.highlight.fulltext.length - 1) { // not the last highlight line
                           res.push({ text: '... ' });
                        }
                        return res;
                     }).map(function(hloa) { return hloa.map(function(hlo) {
                        return hlo.hl ? (
                           <span style={{fontWeight: 'bold'}}>{hlo.text}</span>
                        ) : hlo.text;
                     }) }) : hit._source.fulltext ? (
                           <span>{hit._source.fulltext.substring(0, 512)}</span>
                     ) : (
                           a
                     ) }
                     </div>
                     
                     <div>
                     <span>/<span title={hostPathElt} onClick={() => this.handleBrowseToPath(pathElts, 0)} style={{color:'red'}}>{hit._source.readable_host}</span></span>
                     {pathSpan}
                     <span>/<span onClick={() => this.displayFileDetails()} title={JSON.stringify(hit, null, '\t')} style={{color:'blue'}}>{filePathElt}</span></span>
                     
                     &nbsp;-&nbsp;
                     <span title="find bigger file" onClick={() => this.handleFindBigger(hit._source.content.length)}>{hit._source.content.length} o</span>
                     &nbsp;-&nbsp;
                     <span title={"find modified after " + lastModifiedMoment.format("LLL")} onClick={() => this.handleFindLater(hit._source.file.last_modified)}>{lastModifiedMoment.fromNow()}</span>
                     &nbsp;-&nbsp;
                     <span title={hit._source.fulltext} style={{color:'cyan'}}>Text</span>
                     &nbsp;-&nbsp;
                     <a href={'/file/api/content/' + hit._source.content.store_path}>Cached</a>
                     &nbsp;-&nbsp;
                     <span title={ "find file with same hash" + hit._source.content.hash } onClick={() => this.props.handleSearch({ query : { term: { 'content.hash': hit._source.content.hash } } })} style={{color:'cyan'}}>Same</span>
                     &nbsp;-&nbsp;
                     <span title="find similar documents" onClick={() => this.props.handleSearch({ query : { more_like_this: { like: [{ _index: hit._index, _type: hit._type, _id: hit._id }] } } })} style={{color:'cyan'}}>Similar</span>
                     &nbsp;
                     ({hit._score})
                     </div>
                     
                     </div>
                  );
               }.bind(this))
            }
            </div>
      );
   }
}
SearchBar.defaultProps = {
      results : []
};

ReactDOM.render(
      <SearchApp/>,
      document.querySelector('.container'));