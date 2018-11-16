
import React, { Fragment } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
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
        console.log('constructor Search');
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

        console.log("constructor this.state", this.state);
        console.log("constructor this.props", this.props);
    }


    handleClick(e, index) {
        console.log('handle click');
        e.preventDefault();
        console.log("index =  " + index)
        console.log("currentPage =  " + this.state.currentPage)
        let nextPage = index;
        let nextIndexOfFirstSlice = nextPage * this.state.request.query.size;
        console.log("nextIndexOfFirstSlice = " + nextIndexOfFirstSlice);
        let nextIndexOfLastSlice = (nextPage + 1) * this.state.request.query.size;
        console.log("nextIndexOfLastSlice = " + nextIndexOfLastSlice);
        let nextRequest = this.state.request;
        nextRequest.query.from = nextIndexOfFirstSlice;
        console.log("nextRequest = ", nextRequest);
        this.setState({
            currentPage: nextPage,
            indexOfFirstSlice: nextIndexOfFirstSlice,
            indexOfLastSlice: nextIndexOfLastSlice,
            request: nextRequest
        });
        console.log('handle search update');
        this.handleSearch();
    }

    componentWillReceiveProps(props) {
        console.log("componentWillReceiveProps", props.pageContext);

        let newRequest = {};
        if (props.pageContext) {
            newRequest = props.pageContext.request;
        }
        console.log("newRequest", newRequest);
        let newSize = 0;
        if (props.pageContext && props.pageContext.request && props.pageContext.request.query) {
            newSize = props.pageContext.request.query.size;
        }
        console.log("newsize",newSize);
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
        
        console.log(nextState);
        
    }

    componentWillMount() {
        if (this.props.pageContext && this.props.pageContext.request) {
            console.log('Search Component Will Mount');
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
        console.log('handle search request ', this.state.request);
        axios.post("/search", this.state.request).then(response => {
            this.setState({
                data: response.data,
                total: response.data.hits.total,
                request: this.state.request,
                makeSearch: true
            })
            console.log("data");
            console.log(this.state.data);
            this.render();
        }).catch(error => {
            console.log("error", error);
            this.render();
        });

    }
    getTitle() {
        if (this.props.pageContext.request && this.props.pageContext.request.query && this.request.query.query && this.request.query.query.match && this.request.query.query.match) {
            return this.request.query.query.match.title
        } else {
            return ''
        }
    }

    display(renderSlice) {
        console.log("currentSlice", renderSlice);
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
                            <h4 className="display-6">{data._source.title}</h4>
                            <p className="lead">{data._source.title}</p>
                            <a href={data._source['Content-Location']}>{data._source['Content-Location']}</a>
                            <hr className="my-1" />
                        </Jumbotron>
                    </Row>
                );
            });

            return (
                <section className="container" style={{ backgroundColor: 'rgba(247, 247, 247, 0.39)' }} >

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
                                <Col xs="10">
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
                                <Col xs="2">
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
                                </div>
                            </Fragment>
                        </div>
                    ) : ('')
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