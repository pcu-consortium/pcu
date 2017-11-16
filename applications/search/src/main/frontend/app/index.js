import React, { Component } from 'react';

import ReactDOM from 'react-dom';
import axios from 'axios';
import moment from 'moment';

// React Components
import LoginOverlay from './components/login.js'

// Stylesheets
import '../style/main.css'

var debug = false;

var pcuUrl = '/pcu/';
//var pcuUrl = 'http://localhost:45665/'; // on mock default random port
var searchUrl = pcuUrl + 'search/esapi/'; // on PCU's ElasticSearch-like API impl'd on local ElasticSearch
//var searchUrl = 'http://localhost:9200/'; // directly on local ElasticSearch
//var searchUrl = 'http://localhost:45665/search/elasticsearch'; // on mock default random port
var searchFileUrl = searchUrl + 'files/file/_search'; 
var fileApiUrl = pcuUrl + 'file/api/content/';
   
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
            isActiveFooterMobile: false,
            isActiveOverlay: false,
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
      axios.post(searchFileUrl, queryRequest).then(response => {
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
   toggleModal = () => {
      if(this.state.isActiveOverlay === true) {
            this.setState({
                  isActiveOverlay: false
            });
      } else if (this.state.isActiveOverlay === false) {
            this.setState({
                  isActiveOverlay: true
            });
      }
   }
   toggleFooter = () => {
      if(this.state.isActiveFooterMobile === true) {
            this.setState({
                  isActiveFooterMobile: false
            });
      } else if (this.state.isActiveFooterMobile === false) {
            this.setState({
                  isActiveFooterMobile: true
            });
      }
   }
   render = () => {
      return (
            <div className="appWrapper">
            
            <LoginOverlay isActiveOverlay={this.state.isActiveOverlay} toggleModal={this.toggleModal} />

            <SearchBar
                  handleSearch={this.handleSearch}
                  toggleModal={this.toggleModal} 
                  isActiveFooterMobile={this.state.isActiveFooterMobile} 
                  toggleFooter={this.toggleFooter} 
            />
            
            {/*
            Focus rather than browse / rollover browse mode (facets) / keep previous criteria
            */}

            { this.state.hits.total == 0 ? '' : (
            <div>
            
            { debug ? (
            <div className="resultsNumbers">
                  query: {JSON.stringify(this.state.queryRequest.query, null, '\t')}.
            </div>
            ) : '' }

            {  // && this.state.currentPage != 1
               (this.state.queryRequest.from != 0) ? (
                     <div style={{ paddingLeft: '100px'}}><span title="previous" onClick={() => this.handlePrevious()}>&lt; ...</span></div>
               ) : ''
            }
            <ResultList hits={this.state.hits} took={this.state.took} handleSearch={this.handleSearch} queryRequest={this.state.queryRequest} />
            {
               (this.state.queryRequest.from + this.state.queryRequest.size < this.state.hits.total - 1) ? (
                     <div style={{ paddingLeft: '100px'}}><span title="next" onClick={() => this.handleNext()}>... &gt;</span></div>
               ) : ''
            }

            </div>
            ) }

                  <div className={"footer" + (this.state.isActiveFooterMobile ? ' show' : '')}>
                        <div className="footer__wrapper">
                              <ul className="footer__list">
                              <li><a href="http://pcu-consortium.github.io/">PCU Consortium</a></li>
                              <li><a href="">Documentation</a></li>
                              <li><a href="http://smile.fr/">Support</a></li>
                              {/*}
                              // TODO font awesome in webpack :
                              <li><a>Follow us 
                                 <a alt="Github" href="https://github.com/pcu-consortium" target="_blank"><i class="fa fa-github fa-2x"></i></a>
                                 <a alt="Twitter" href="https://twitter.com/PCUConsortium" target="_blank"><i class="fa fa-twitter fa-2x"></i></a>
                                 <a alt="Facebook" href="https://facebook.com/pcu-consortium" target="_blank"><i class="fa fa-facebook fa-2x"></i></a>
                                 <a alt="Linkedin" href="https://www.linkedin.com/company/pcu-consortium" target="_blank"><i class="fa fa-linkedin fa-2x"></i></a>
                                 <a alt="Slideshare" href="https://www.slideshare.net/pcuconsortium" target="_blank"><i class="fa fa-slideshare fa-2x"></i></a>
                              </a></li>
                              */}
                              <li><a>© 2017 PCU Consortium</a></li>
                              </ul>
                        </div>
                  </div>

            </div>
            
      );
   }
}

class SearchBar extends React.Component {
   constructor(props) {
      super(props);
      this.state = {
            searchText : "",
            showClearButton : false
      };
   }
   onSearchTextChange = (e) => {
      var searchText = e.target.value;
      this.setState((prevState) => ({...prevState, searchText:searchText })); // searchText:e.target.value KO !
      console.log("onSearchTextChange", this.state, e);
      this.isFilled(e);
   }
   isFilled = (e) => {
      if(e.target.value !== "") {
            this.setState({ showClearButton: true});
      }
      else {
            this.setState({ showClearButton: false});
      }
   }
   clearField = () => {
      this.refs.inputSearch.value = '';
   }
   handleSearchText = () => {
      this.props.handleSearch({ query: { bool: { should: [
         { multi_match: { query: this.state.searchText, fields: [ "path.tree^0.5", "file.name", "meta.author", "meta.title", "fulltext^0.8" ] } }, // analyzed
         { terms: { "path.tree^0.5": [this.state.searchText] } } // not analyzed TODO NOO better
         ] } }, highlight: { fields: { fulltext: { } } } }); // unified highlighter is default, no need for type: 'unified'
   }
   onChangeSortFilter= (e) => {
      if(e.target.value === 'date') {
            this.props.handleSearch({from: 0, size: 10, sort: {'file.last_modified': 'desc'}}, true);
      } else if (e.target.value === 'relevance') {
            this.props.handleSearch({from: 0, size: 10, sort: {'_score': 'desc'}}, true);
            // is default but has to be set in ordere to override previous one. NB. , merely sort: ['_score'] NO SUPPORTED by PCU ES API
      }
   }
   render() {
      return (
            <div className="searchBar">
                  <div className="searchBar__wrapperSearch--large">
                        <button className={"searchBar__toggleFooter" + (this.props.isActiveFooterMobile ? ' closeFooter' : ' openFooter')} onClick={this.props.toggleFooter}>
                              <img src="/img/burger.svg" alt="Footer Menu" width="22" className="burger" />
                              <img src="/img/white_suppr.svg" alt="Close Footer Menu" width="22" className="suppr" />
                        </button>      
                        <div className="searchBar__logo">
                              <img src="/img/logo.png" alt="LOGO PCU" width="69" />
                        </div>
                        <div className="searchBar__bar">
                              <input placeholder="John Doe" onChange={(e) => this.onSearchTextChange(e)} ref="inputSearch"/>
                              <button class="SearchBtn" onClick={this.handleSearchText}>
                                    <img src="/img/loupe.svg"/>
                              </button>
                              <button className={"cancelSearchBtn" +  (this.state.showClearButton ? ' show' : '')} onClick={this.clearField}>
                                    <img src="/img/suppr.svg" />
                              </button>
                        </div>
                        <div className="searchBar__menuList">
                              <ul>
                                    <li><a href="">Overview</a></li>
                                    <li><a href="">Use Cases</a></li>
                                    <li><a href="">Resources</a></li>
                                    <li><a href="">News</a></li>
                                    <li><a href="">Community</a></li>
                              </ul>
                        </div>
                        <div className="searchBar__loginArea">
                              <button className="btnLogin" onClick={this.props.toggleModal}>
                                    <img src="/img/login.svg" alt="LOG IN LOCK" width="17" height="21" />
                                    LOG IN
                              </button>
                        </div>
                  </div>      
                  <div className="searchBar__searchFilter">
                     <div className="searchBar__searchFilter__wrapper">   
                       <ul>
                              <li onClick={this.handleSearchText}>Search</li>
                              <li>Images</li>
                              <li>Advanced</li>
                              <li>Tips</li>
                        </ul>
                        <div className="selectSortFilter__wrapper">
                              <div>
                              <select onClick={this.onChangeSortFilter} className="selectSortFilter">
                                    <option style={{display: 'none'}} value='Sort By'>Sort By</option>
                                    <option value='date'>Date</option>
                                    <option value='relevance'>Relevance</option>
                              </select>
                              </div>
                        </div>
                     </div>    
                  </div>
            </div>
      );
   }
}

class ResultList extends React.Component {
   constructor(props) {
      super(props);
      this.state = {
            currentPage: 1,
            isLoadedContent: false,
            currentTitle: ''
          };
   }
   displayFileDetails = () => {
      console.log("displayFileDetails");
   }
   fileDetailsLoad = (title) => {
      this.setState({ isLoadedContent: true} );
      this.setState({ currentTitle: title})
   }
   fileDetailsClose = () => {
      this.setState({ isLoadedContent: false});
   }
   handleBrowseToPath = (pathElts, depth) => {
      this.props.handleSearch({ query: { terms: { 'path.tree': ['/' + pathElts.slice(0, depth + 1).join('/')] } } }); // file.path.tree '/AkIesvz+/home/mardut/dev/pcu/workshop_elastic/kibana-5.2.2-linux-x86_64/README.txt'
   }
   handleFindLater = (lastModified) => {
      this.props.handleSearch({ query: { range: { 'file.last_modified': { gt: lastModified } } } });
   }
   handleFindBigger = (length) => {
      this.props.handleSearch({ query: { range: { 'content.length': { gt: length } } } });
   }
   handleFindSameHash = (hash) => {
      this.props.handleSearch({ query: { terms: { 'content.hash': [hash] } } });
   }
   render() {
      return (
            <div className="results">
                  <div className="results__colLeft">

                        <div className="resultsNumbers">
                              Results {this.props.queryRequest.from + 1}-{this.props.queryRequest.from + this.props.queryRequest.size} of {this.props.hits.total}.
                              Search took {this.props.took}ms.
                        </div>

                        {
                        this.props.hits.hits.map(function(hit, hitInd) {
                              var path = hit._source.path;
                              var pathElts = path.substring(1).split('/'); // remove root / first
                              var hostPathElt = pathElts[0];
                              var filePathElt = pathElts[pathElts.length - 1];
                              var pathSpan = pathElts.slice(1, -1).map(function(pathElt, pathEltInd) { // remove then special cases (host & file)
                              return (
                                    <span key={pathEltInd}>/<span title="browse to path" onClick={() => this.handleBrowseToPath(pathElts, pathEltInd + 1)}>{pathElt}</span></span>
                              );
                              }.bind(this));
                              
                              var title = hit._source.meta.title ? hit._source.meta.title : filePathElt;
                              // or any other metas, including for dir : Index of x Name Last modified Size Parent [DIR]
                              
                              var lastModifiedMoment = moment(hit._source.file.last_modified); // "2011-04-11T10:20:30Z" "2016-10-01T15:29:45.000+0000"
                              
                              return (
                                    <div className="resultsItem" key={hitInd}>
                                    
                                    <div className="resultsItem__title">
                                    [{t[hit._source.http.mimetype]}]
                                    &nbsp;
                                    <a href={hit._source.http.url} title={JSON.stringify(hit, null, '\t')}>{title}</a>
                                    </div>
                                    
                                    <div className="resultsItem__description" onClick={() => this.fileDetailsLoad(title)}>
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
                                    }).map(function(hloa) { return hloa.map(function(hlo, hloInd) {
                                          return hlo.hl ? (
                                          <span style={{fontWeight: 'bold'}} key={hloInd}>{hlo.text}</span>
                                          ) : hlo.text;
                                    }) }) : (
                                          <span>{hit._source.fulltext ? hit._source.fulltext.substring(0, 512) : ''}</span>
                                    ) }
                                    </div>
                                    
                                    <div className="resultsItem__extraInformations">    
                                          <span>/<span title={hostPathElt} onClick={() => this.handleBrowseToPath(pathElts, 0)}>{hit._source.readable_host}</span></span>
                                          {pathSpan}
                                          <span>/<span onClick={() => this.displayFileDetails()} title={JSON.stringify(hit, null, '\t')}>{filePathElt}</span></span>
                                          &nbsp;-&nbsp;
                                          <span title="find bigger file" onClick={() => this.handleFindBigger(hit._source.content.length)}>{hit._source.content.length} octet</span>
                                          &nbsp;-&nbsp;
                                          <span className="resultsItem__date" title={"find modified after " + lastModifiedMoment.format("LLL")} onClick={() => this.handleFindLater(hit._source.file.last_modified)}>{lastModifiedMoment.fromNow()}</span>
                                    </div>
                                    <div className="resultsItem__filtersMenu">
                                          <span title={hit._source.fulltext ? hit._source.fulltext : ''}>Text</span>
                                          &nbsp;-&nbsp;
                                          <a href={fileApiUrl + hit._source.content.store_path}>Cached</a>
                                          &nbsp;-&nbsp;
                                          <span title={ "find file with same hash" + hit._source.content.hash } onClick={() => this.handleFindSameHash(hit._source.content.hash)}>Same</span>
                                          &nbsp;-&nbsp;
                                          <span title="find similar documents" onClick={() => this.props.handleSearch({ query : { more_like_this: { like: [{ _index: hit._index, _type: hit._type, _id: hit._id }] } } })}>Similar</span>
                                          &nbsp;
                                          ({hit._score})
                                    </div>   
                                    </div>
                              );
                        }.bind(this))
                        }
                  </div>
                  <div className="results__colRight">
                     <div className={"colRight__wrapper" +  (this.state.isLoadedContent ? ' show' : '')}>   
                        <h3 className="document__title">{ this.state.currentTitle }</h3>
                        <button className="closeDocumentBtn" onClick={() => this.fileDetailsClose()}>
                              <img src="/img/suppr.svg" alt="Close Button" />
                        </button>
                        <div className="document__wrapper">
                              <div className="document__content">
                                    <img src="http://via.placeholder.com/400x520" alt="Document Content" />
                              </div>
                        </div>    
                     </div>     
                  </div>
            </div>
      );
   }
}

SearchBar.defaultProps = {
      results : []
};

ReactDOM.render(
      <SearchApp/>,
      document.querySelector('.app'));