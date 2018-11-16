
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
    Label,
    Input
} from 'reactstrap';
const jumbotron = { backgroundColor: 'rgba(243, 243, 243, 0.8)', padding: 0, width: "100%" };
const styleResultDetail = { color: 'rgba(120, 120, 120, 0.40)' };
class Search extends React.Component {
    constructor(props) {
        super(props);

        this.request = this.props.request
        this.size = this.props.request.query.size;

        this.state = {
            currentPage: 0,
            indexOfFirstSlice: 0,
            indexOfLastSlice: this.props.request.query.size,
            data: { hits: { hits: [], total: 0 }, took: 0 },
            request: this.props.request
        };
        this.handleSearch = this.handleSearch.bind(this);
        this.changeNumberPage = this.changeNumberPage.bind(this);
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

    componentWillReceiveProps() {
        this.setState({
            request: this.props.request,
        });
    }
    componentWillMount() {
        axios.post("/search", this.state.request).then(response => {
            this.setState({
                data: response.data,
                request: this.props.request
            });
        }).catch(error => {
            console.log("error", error);
        });
    }

    handleSearch() {
        console.log('handle search');
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
        const { indexOfFirstSlice, indexOfLastSlice, currentPage } = this.state;
        let totalResults = this.state.data.hits.total;
        let requestSize = this.state.request.query.size;
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
                <div style={{ padding: "20px 0px 0px 0px" }}>
                    <h6 >Query : {this.request.query.query.match.title}</h6>
                </div>
                <span style={styleResultDetail}> {totalResults === 0 ?
                    (
                        <Alert color="warning">
                            No Result Found
                        </Alert>
                    ) : ("Results :" + (indexOfFirstSlice + 1) + "-" + (indexOfLastSlice < totalResults ? indexOfLastSlice : totalResults) + " of " + totalResults + " , Search took " + this.state.data.took + "ms.")
                }
                </span>

                <div className="pagination-wrapper">
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
                </div>
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
            </section >
        )
    }
}

export default Search;