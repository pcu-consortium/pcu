
import React, { Fragment } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './Colors.js';
import { Alert } from 'reactstrap';
import axios from 'axios';
import {
    Pagination,
    PaginationItem,
    PaginationLink,
    Container,
    Row,
    Col,
    Jumbotron,
    Input
} from 'reactstrap';

const jumbotron = { backgroundColor: 'rgba(243, 243, 243, 0.8)', padding: 0, width: "100%" };
const styleResultDetail = { color: 'rgba(120, 120, 120, 0.40)' };
class Search extends React.Component {
    constructor(props) {
        super(props);
        this.handleSearch = this.handleSearch.bind(this);
        this.changeNumberPage = this.changeNumberPage.bind(this);

        this.state = {
            currentPage: 0,
            indexOfFirstSlice: 0,
            indexOfLastSlice: 0,
            data: { hits: { hits: [], total: 0 }, took: 0 },
            request: {}
        };

    }


    handleClick(e, index) {
        e.preventDefault();
        let nextPage = index;
        let nextIndexOfFirstSlice = nextPage * this.state.request.query.size;
        let nextIndexOfLastSlice = (nextPage + 1) * this.state.request.query.size;
        let nextRequest = this.state.request;
        nextRequest.query.from = nextIndexOfFirstSlice;
        this.setState({
            currentPage: nextPage,
            indexOfFirstSlice: nextIndexOfFirstSlice,
            indexOfLastSlice: nextIndexOfLastSlice,
            request: nextRequest
        });
        this.handleSearch();
    }

    componentWillReceiveProps(props) {

        let newRequest = {};
        if (props.pageContext) {
            newRequest = props.pageContext.request;
        }
        let newSize = 0;
        if (props.pageContext && props.pageContext.request && props.pageContext.request.query) {
            newSize = props.pageContext.request.query.size;
        }
        // merge
        let nextState = {
            ...this.state, ...{
                pageContext: props.pageContext,
                size: newSize,
                request: newRequest
            }
        };
        this.setState(nextState, function () {
            this.handleSearch();
        });
    }

    componentWillMount() {
        if (this.props.pageContext && this.props.pageContext.request) {
            axios.post("/search", this.state.request).then(response => {
                this.setState({
                    data: response.data,
                    request: this.props.pageContext.request
                });
            }).catch(error => {
                console.log("error", error);
            });
        }
    }

    handleSearch() {
        if (this.props.pageContext.activeTab === 'search' && this.state.request !== undefined) {
            axios.post("/search", this.state.request).then(response => {
                this.setState({
                    data: response.data,
                    total: response.data.hits.total,
                    request: this.state.request,
                    makeSearch: true
                })
                this.render();
            }).catch(error => {
                console.log("error", error);
                this.render();
            });
        }
    }
    getTitle() {
        if (this.props.pageContext.request && this.props.pageContext.request.query && this.request.query.query && this.request.query.query.match && this.request.query.query.match) {
            return this.request.query.query.match.title
        } else {
            return ''
        }
    }

    changeNumberPage(e) {
        let changeRequest = this.state.request;
        changeRequest.query.size = Number(e.target.value);
        let nextIndexOfLastSlice = changeRequest.query.size;
        this.setState({
            currentPage: 0,
            indexOfFirstSlice: 0,
            indexOfLastSlice: nextIndexOfLastSlice,
            request: changeRequest
        });
        this.handleSearch();
    }
    render() {
        if (this.props.pageContext && this.props.pageContext.request) {
            const { indexOfFirstSlice, indexOfLastSlice, currentPage } = this.state;
            let totalResults = this.state.data.hits.total;
            let requestSize = 0;
            if (this.props.pageContext.request && this.props.pageContext.request.query) {
                requestSize = this.state.request.query.size;
            }
            const pageNumbers = [];
            const pagesCount = Math.ceil(totalResults / requestSize);

            for (let i = 1; i <= pagesCount; i++) {
                pageNumbers.push(i);
            }

            const renderData = this.state.data.hits.hits.map((data, index) => {
                return (
                    <Row key={index}>
                        <Jumbotron style={jumbotron} className="shadow">
                            <h4 className="display-6" style={{ cursor: "pointer" }} onClick={() => window.open(data._source['Content-Location'], "_blank")}>{data._source.title}</h4>
                            <p className="lead">{data._source.title}</p>
                            <div style={{ width: "100%", overflow: "auto" }}>
                                <a target="_blank" rel="noopener noreferrer" style={{ color: "#337ab7" }} href={data._source['Content-Location']}>{data._source['Content-Location']}</a>
                            </div>
                            <hr className="my-1" />
                            <p>{data._source.title}</p>
                        </Jumbotron>
                    </Row>
                );
            });

            return (
                <section className="container" style={{ backgroundColor: 'rgba(247, 247, 247, 0.39)'}} >

                    {totalResults !== 0 ? (
                        <div className="pagination-wrapper">
                            <div style={{ padding: "20px 0px 0px 0px" }}>
                                <h6 >Query : {this.state.request.query.query.match.title}</h6>
                            </div>
                            <span style={styleResultDetail}> {totalResults === 0 ?
                                (
                                    <Alert color="warning">
                                        No Result Found
                                    </Alert>
                                ) : ("Results :" + (indexOfFirstSlice + 1) + "-" + (indexOfLastSlice < totalResults ? indexOfLastSlice : totalResults) + " of " + totalResults + " , Search took " + this.state.data.took + "ms.")
                            }
                            </span>
                            <Row>
                                <Col xs="9" style={{ width: "100%", overflow: "auto" }} >
                                    <Pagination aria-label="Page navigation example">
                                        <PaginationItem disabled={currentPage <= 0}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage - 1)}
                                                previous
                                                href="#"
                                            />
                                        </PaginationItem>

                                        {pageNumbers.map((page, i) =>
                                            <PaginationItem active={i === currentPage} key={i}>
                                                <PaginationLink onClick={e => this.handleClick(e, i)} href="#">
                                                    {i + 1}
                                                </PaginationLink>
                                            </PaginationItem>
                                        )}

                                        <PaginationItem disabled={currentPage >= pagesCount - 1}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage + 1)}
                                                next
                                                href="#"
                                            />
                                        </PaginationItem>
                                    </Pagination>
                                </Col>
                                <Col xs="3">
                                    <Input type="select" name="select" onChange={this.changeNumberPage} >
                                        <option value="10">10</option>
                                        <option value="20">20</option>
                                        <option value="25">25</option>
                                        <option value="50">50</option>
                                    </Input>
                                </Col>
                            </Row>
                            <Fragment>
                                <div>
                                    <Container >
                                        {renderData}
                                    </Container >
                                </div >
                                <div className="pagination-wrapper">

                                    <Pagination aria-label="Page navigation example" style={{ width: "100%", overflow: "auto" }}>

                                        <PaginationItem disabled={currentPage <= 0}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage - 1)}
                                                previous
                                                href="#"
                                            />
                                        </PaginationItem>

                                        {pageNumbers.map((page, i) =>
                                            <PaginationItem active={i === currentPage} key={i}>
                                                <PaginationLink onClick={e => this.handleClick(e, i)} href="#">
                                                    {i + 1}
                                                </PaginationLink>
                                            </PaginationItem>
                                        )}

                                        <PaginationItem disabled={currentPage >= pagesCount - 1}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage + 1)}
                                                next
                                                href="#"
                                            />
                                        </PaginationItem>
                                    </Pagination>
                                </div>
                            </Fragment>
                        </div>
                    ) : (
                            <Alert color="warning" style={{ marginTop: '1rem'}}>
                                No Result Found
                            </Alert>
                        )
                    }

                </section >
            )
        }
        else {
            return (
                <Row >
                    Search something
                </Row>
            );
        }
    }
}

export default Search;