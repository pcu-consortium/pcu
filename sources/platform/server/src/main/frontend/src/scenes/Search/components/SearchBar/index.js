import React from "react";

class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            searchText: "",
            showClearButton: false
        };
    }
    onSearchTextChange = (e) => {
        var searchText = e.target.value;
        this.setState((prevState) => ({ ...prevState, searchText: searchText })); // searchText:e.target.value KO !
        console.log("onSearchTextChange", this.state, e);
        this.isFilled(e);
    }
    isFilled = (e) => {
        if (e.target.value !== "") {
            this.setState({ showClearButton: true });
        }
        else {
            this.setState({ showClearButton: false });
        }
    }
    clearField = () => {
        this.refs.inputSearch.value = '';
    }
    handleSearchText = () => {
        this.props.handleSearch({
            query: {
                bool: {
                    should: [
                        { multi_match: { query: this.state.searchText, fields: ["path.tree^0.5", "file.name", "meta.author", "meta.title", "fulltext^0.8"] } }, // analyzed
                        { terms: { "path.tree^0.5": [this.state.searchText] } } // not analyzed TODO NOO better
                    ]
                }
            }, highlight: { fields: { fulltext: {} } }
        }); // unified highlighter is default, no need for type: 'unified'
    }
    onChangeSortFilter = (e) => {
        if (e.target.value === 'date') {
            this.props.handleSearch({ from: 0, size: 10, sort: { 'file.last_modified': 'desc' } }, true);
        } else if (e.target.value === 'relevance') {
            this.props.handleSearch({ from: 0, size: 10, sort: { '_score': 'desc' } }, true);
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
                        <input placeholder="John Doe" onChange={(e) => this.onSearchTextChange(e)} ref="inputSearch"
                            onKeyPress={e => { if (e.key === 'Enter') { this.handleSearchText() } }} />
                        <button className="SearchBtn" onClick={this.handleSearchText}>
                            <img src="/img/loupe.svg" alt="loupe"/>
                        </button>
                        <button className={"cancelSearchBtn" + (this.state.showClearButton ? ' show' : '')} onClick={this.clearField}>
                            <img src="/img/suppr.svg" alt="suppr"/>
                        </button>
                    </div>
                    <div className="searchBar__menuList">
                        <ul>
                            <li><a href="/">Overview</a></li>
                            <li><a href="/">Use Cases</a></li>
                            <li><a href="/">Resources</a></li>
                            <li><a href="/">News</a></li>
                            <li><a href="/">Community</a></li>
                        </ul>
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
                                    <option style={{ display: 'none' }} value='Sort By'>Sort By</option>
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

export default SearchBar; 