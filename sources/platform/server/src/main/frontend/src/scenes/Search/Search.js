import React, { Component } from 'react';
import axios from 'axios';
import SearchBar from './components/SearchBar';
import SearchResult from './components/SearchResult';


const initialState = {
    loading: false,
    loaded: true,
    isActiveFooterMobile: false,
    isActiveOverlay: false,
    queryRequest: { query: {}, from: 0, size: 10 },
    hits: { hits: [], total: 0 },
    took: 0
};

var debug = false;
var pcuUrl = '/pcu/';
var searchUrl = pcuUrl + 'search/esapi/'; // on PCU's ElasticSearch-like API impl'd on local ElasticSearch
var searchFileUrl = searchUrl + 'files/file/_search'; 


class Search extends Component {
    state = initialState

    handleSearch = (queryRequest, merge) => {
        console.log("handleSearch");
        if (merge) {
            queryRequest = { ...this.state.queryRequest, ...queryRequest, query: { ...this.state.queryRequest.query, ...queryRequest.query } }
        } else {
            queryRequest.from = 0;
            queryRequest.size = 10;
        }
        this.setState(prevState => ({ ...prevState, queryRequest: queryRequest }));
        axios.post(searchFileUrl, queryRequest).then(response => {
            this.setState(prevState => ({ ...prevState, hits: response.data.hits, took: response.data.took }));
            console.log("response state", this.state);
        }).catch(error => {
            console.log("error", error);
        });
    }

    handlePrevious = () => {
        var size = this.state.queryRequest.size;
        this.handleSearch({ ...this.state.queryRequest, from: this.state.queryRequest.from - size, size: size }, true);
    }
    handleNext = () => {
        var size = this.state.queryRequest.size;
        this.handleSearch({ ...this.state.queryRequest, from: this.state.queryRequest.from + size, size: size }, true);
    }

    toggleModal = () => {
        if (this.state.isActiveOverlay === true) {
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
        if (this.state.isActiveFooterMobile === true) {
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

                <SearchBar
                    handleSearch={this.handleSearch}
                    toggleModal={this.toggleModal}
                    isActiveFooterMobile={this.state.isActiveFooterMobile}
                    toggleFooter={this.toggleFooter}
                />

                {/*
              Focus rather than browse / rollover browse mode (facets) / keep previous criteria
              */}

                {this.state.hits.total === 0 ? '' : (
                    <div>

                        {debug ? (
                            <div className="resultsNumbers">
                                query: {JSON.stringify(this.state.queryRequest.query, null, '\t')}.
                            </div>
                        ) : ''}

                        {  // && this.state.currentPage != 1
                            (this.state.queryRequest.from !== 0) ? (
                                <div style={{ paddingLeft: '100px' }}><span title="previous" onClick={() => this.handlePrevious()}>&lt; ...</span></div>
                            ) : ''
                        }
                        <SearchResult hits={this.state.hits} took={this.state.took} handleSearch={this.handleSearch} queryRequest={this.state.queryRequest} />
                        {
                            (this.state.queryRequest.from + this.state.queryRequest.size < this.state.hits.total - 1) ? (
                                <div style={{ paddingLeft: '100px' }}><span title="next" onClick={() => this.handleNext()}>... &gt;</span></div>
                            ) : ''
                        }

                    </div>
                )}

                <div className={"footer" + (this.state.isActiveFooterMobile ? ' show' : '')}>
                    <div className="footer__wrapper">
                        <ul className="footer__list">
                            <li><a href="http://pcu-consortium.github.io/">PCU Consortium</a></li>
                            <li><a href="/">Documentation</a></li>
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
                            <li>© 2017 PCU Consortium</li>
                        </ul>
                    </div>
                </div>

            </div>

        );
    }
}

export default Search
