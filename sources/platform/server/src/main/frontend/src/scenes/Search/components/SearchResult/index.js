import React from "react";
import moment from 'moment';

var t = {
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'DOC',
    'application/pdf': 'PDF',
}

var pcuUrl = '/pcu/';
var fileApiUrl = pcuUrl + 'file/api/content/';

class SearchResult extends React.Component {
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
        this.setState({ isLoadedContent: true });
        this.setState({ currentTitle: title })
    }
    fileDetailsClose = () => {
        this.setState({ isLoadedContent: false });
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
    handleFindSimilar = (hit) => {
        this.props.handleSearch({
            query: {
                more_like_this: {
                    fields: ["fulltext"], // way faster than on all fields (default)
                    like: [{ _index: hit._index, _type: hit._type, _id: hit._id }]
                }
            }
        });
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
                        this.props.hits.hits.map(function (hit, hitInd) {
                            var path = hit._source.path;
                            var pathElts = path.substring(1).split('/'); // remove root / first
                            var hostPathElt = pathElts[0];
                            var filePathElt = pathElts[pathElts.length - 1];
                            var pathSpan = pathElts.slice(1, -1).map(function (pathElt, pathEltInd) { // remove then special cases (host & file)
                                return (
                                    <span key={pathEltInd}>/<span title="browse to path" onClick={() => this.handleBrowseToPath(pathElts, pathEltInd + 1)}>{pathElt}</span></span>
                                );
                            }.bind(this));

                            var title = hit._source.meta.title ? hit._source.meta.title : filePathElt;
                            // or any other metas, including for dir : Index of x Name Last modified Size Parent [DIR]

                            var lastModifiedMoment = moment(hit._source.file.last_modified); // "2011-04-11T10:20:30Z" "2016-10-01T15:29:45.000+0000"

                            var fileExtension = filePathElt.substring(filePathElt.lastIndexOf('.') + 1, filePathElt.length);
                            var mimetype = fileExtension;
                            var displayedMimetype = fileExtension;
                            if (hit._source.http && hit._source.http.mimetype) {
                                mimetype = hit._source.http.mimetype;
                                if (t[mimetype]) {
                                    displayedMimetype = t[mimetype];
                                }
                            }

                            return (
                                <div className="resultsItem" key={hitInd}>

                                    <div className="resultsItem__title">
                                        [<span title={mimetype}>{displayedMimetype}</span>]
                                        &nbsp;
                                     <a href={hit._source.http && hit._source.http.url ? hit._source.http.url : ''} title={JSON.stringify(hit, null, '\t')}>{title}</a>
                                    </div>

                                    <div className="resultsItem__description" onClick={() => this.fileDetailsLoad(title)}>
                                        {hit.highlight ? hit.highlight.fulltext.map(function (hl, hlInd) {
                                            var res = [];
                                            var emInd = 0;
                                            var endEmInd;
                                            while ((emInd = hl.indexOf('<em>', emInd)) !== -1) {
                                                res.push({ text: hl.substring(endEmInd, emInd) });
                                                endEmInd = hl.indexOf('</em>', emInd)
                                                res.push({ hl: true, text: hl.substring(emInd + 4, endEmInd) });
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
                                        }).map(function (hloa) {
                                            return hloa.map(function (hlo, hloInd) {
                                                return hlo.hl ? (
                                                    <span style={{ fontWeight: 'bold' }} key={hloInd}>{hlo.text}</span>
                                                ) : hlo.text;
                                            })
                                        }) : (
                                                <span>{hit._source.fulltext ? hit._source.fulltext.substring(0, 512) : ''}</span>
                                            )}
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
                                           <span title={"find file with same hash" + hit._source.content.hash} onClick={() => this.handleFindSameHash(hit._source.content.hash)}>Same</span>
                                        &nbsp;-&nbsp;
                                           <span title="find similar documents" onClick={() => this.handleFindSimilar(hit)}>Similar</span>
                                        &nbsp;
                                           ({hit._score})
                                     </div>
                                </div>
                            );
                        }.bind(this))
                    }
                </div>
                <div className="results__colRight">
                    <div className={"colRight__wrapper" + (this.state.isLoadedContent ? ' show' : '')}>
                        <h3 className="document__title">{this.state.currentTitle}</h3>
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

export default SearchResult;