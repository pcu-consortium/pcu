
import React, { Fragment } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { PcuBackgroundCode, PcuGreyCode, PcuBlueCode, PcuGreenCode } from './Colors.js';
import { Alert } from 'reactstrap';
import axios from 'axios';
import {
    Pagination,
    PaginationItem,
    PaginationLink,
    Container,
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    CardSubtitle,
    CardText,
    CardLink,
    Badge,
    Input
} from 'reactstrap';

const pagination = { backgroundColor: PcuGreenCode, color: PcuBackgroundCode };
const activePagination = { ...pagination, backgroundColor: PcuBlueCode };
const styleResultDetail = { color: PcuBlueCode };
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

    getTitle(data) {
        if (data._source.title !== undefined) {
            return data._source.title;
        } else if (data._source['dc:title'] !== undefined) {
            return data._source['dc:title'];
        } else if (data._source['og:title'] !== undefined) {
            return data._source['og:title'];
        }
    }

    getSubtitle(data) {
        if (data._source.subject !== undefined) {
            return data._source.subject;
        } else if (data._source['dc:subject'] !== undefined) {
            return data._source['dc:subject'];
        } else if (data._source['og:site_name'] !== undefined) {
            return data._source['og:site_name'];
        }
    }

    getDescription(data) {
        if (data._source.subject !== undefined) {
            return data._source.subject;
        } else if (data._source['dc:description'] !== undefined) {
            return data._source['dc:description'];
        } else if (data._source['og:description'] !== undefined) {
            return data._source['og:description'];
        }
    }

    getUrl(data) {
        if (data._source['Content-Location'] !== undefined) {
            return data._source['Content-Location'];
        } else if (data._source['dc:source'] !== undefined) {
            return data._source['dc:source'];
        } else if (data._source['og:url'] !== undefined) {
            return data._source['og:url'];
        }
    }

    getBadges(data) {
        let badges = [];

        if (data._source['document.contentType'] !== undefined) {
            badges.push(data._source['document.contentType']);
        } else if (data._source['dc:format'] !== undefined) {
            badges.push(data._source['dc:format']);
        }

        if (data._source['dc:type'] !== undefined) {
            badges.push(data._source['dc:type']);
        } else if (data._source['og:type'] !== undefined) {
            badges.push(data._source['og:type']);
        }
        return badges;
    }

    pagination(currentPage, nrOfPages) {
        var delta = 2,
            range = [],
            rangeWithDots = [],
            l;

        range.push(1);

        if (nrOfPages <= 1) {
            return range;
        }

        for (let i = currentPage - delta; i <= currentPage + delta; i++) {
            if (i < nrOfPages && i > 1) {
                range.push(i);
            }
        }
        range.push(nrOfPages);

        for (let i of range) {
            if (l) {
                if (i - l === 2) {
                    rangeWithDots.push(l + 1);
                } else if (i - l !== 1) {
                    rangeWithDots.push('...');
                }
            }
            rangeWithDots.push(i);
            l = i;
        }

        return rangeWithDots;
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
            const pagesCount = Math.ceil(totalResults / requestSize);
            const pageNumbers = this.pagination(currentPage + 1, pagesCount);


            const renderDataCards = this.state.data.hits.hits.map((data, index) => {
                return (
                    <Card key={'card' + index} style={{ color: PcuBlueCode, marginTop: '1rem', marginBottom: '1rem' }} id={data._id}>
                        <CardBody>
                            <CardTitle>{this.getTitle(data)}</CardTitle>
                            {this.getSubtitle(data) !== undefined ?
                                (
                                    <CardSubtitle style={{ color: PcuGreenCode }}>{this.getSubtitle(data)}</CardSubtitle>
                                ) : ('')
                            }
                            {this.getDescription(data) !== undefined ?
                                (
                                    <CardText>{this.getDescription(data)}</CardText>
                                ) : ('')
                            }
                            {this.getUrl(data) !== undefined ?
                                (
                                    <CardLink href={this.getUrl(data)} target="_blank">{this.getUrl(data)}</CardLink>
                                ) : ('')
                            }
                            {this.getBadges(data).size !== 0 ?
                                (
                                    <CardText>
                                        {this.getBadges(data).map((badge, i) => {
                                            return (
                                                <Badge style={{ backgroundColor: PcuGreenCode, margin: '0.1rem' }} key={'card' + index + 'badge' + i}>{badge}</Badge>
                                            )
                                        }
                                        )}
                                    </CardText>
                                ) : ('')
                            }
                        </CardBody>
                    </Card>
                );

            });

            return (
                <section className="container" style={{ backgroundColor: PcuBackgroundCode }} >

                        <div className="pagination-wrapper">
                            <div style={{ padding: "20px 0px 0px 0px", color: PcuBlueCode }}>
                                <h6>Query : {this.state.request.query.query.match.title}</h6>
                            </div>
                            {totalResults === 0 ?
                            (
                            <Alert style={{ marginTop: '1rem', backgroundColor: PcuGreyCode, color: PcuBlueCode }}>
                                    No Result Found
                            </Alert>
                             ) :
                            (
                                <div>
                            <span style={styleResultDetail}> 
                                Results : {(indexOfFirstSlice + 1)} - {(indexOfLastSlice < totalResults ? indexOfLastSlice : totalResults)} of {totalResults}. Search took {this.state.data.took} ms.
                            
                            </span>
                            <Row>
                                <Col xs="9" style={{ width: "100%", overflow: "auto" }} >
                                    <Pagination aria-label="Results" size="sm">
                                        <PaginationItem disabled={currentPage <= 0}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage - 1)}
                                                previous
                                                href="#"
                                                style={pagination}
                                            />
                                        </PaginationItem>

                                        {pageNumbers.map((page, i) =>
                                            <PaginationItem
                                                active={page - 1 === currentPage}
                                                key={(page === '...') ? 'top' + page + i : 'top' + (page - 1)}
                                                disabled={page === '...'}>
                                                <PaginationLink
                                                    onClick={e => this.handleClick(e, page - 1)}
                                                    href="#"
                                                    style={page - 1 === currentPage ? (activePagination) : (pagination)} >
                                                    {page}
                                                </PaginationLink>
                                            </PaginationItem>
                                        )}

                                        <PaginationItem disabled={currentPage >= pagesCount - 1}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage + 1)}
                                                next
                                                href="#"
                                                style={pagination}
                                            />
                                        </PaginationItem>
                                    </Pagination>
                                </Col>
                                <Col xs="3">
                                    <Input type="select" name="select" onChange={this.changeNumberPage} bsSize="sm">
                                        <option value="10">10</option>
                                        <option value="20">20</option>
                                        <option value="25">25</option>
                                        <option value="50">50</option>
                                    </Input>
                                </Col>
                            </Row>
                            <Fragment>
                                <div>
                                    <Container>
                                        {renderDataCards}
                                    </Container>
                                </div >
                                <div className="pagination-wrapper">

                                    <Pagination aria-label="Page navigation example" style={{ width: "100%", overflow: "auto" }} size="sm">

                                        <PaginationItem disabled={currentPage <= 0}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage - 1)}
                                                previous
                                                href="#"
                                                style={pagination}
                                            />
                                        </PaginationItem>

                                        {pageNumbers.map((page, i) =>
                                            <PaginationItem
                                                active={page - 1 === currentPage}
                                                key={(page === '...') ? 'bottom' + page + i : 'bottom' + (page - 1)}
                                                disabled={page === '...'}>
                                                <PaginationLink
                                                    onClick={e => this.handleClick(e, page - 1)}
                                                    href="#"
                                                    style={page - 1 === currentPage ? (activePagination) : (pagination)} >
                                                    {page}
                                                </PaginationLink>
                                            </PaginationItem>
                                        )}

                                        <PaginationItem disabled={currentPage >= pagesCount - 1}>
                                            <PaginationLink
                                                onClick={e => this.handleClick(e, currentPage + 1)}
                                                next
                                                href="#"
                                                style={pagination}
                                            />
                                        </PaginationItem>
                                    </Pagination>
                                </div>
                            </Fragment>
                            </div>
                            )}


                        </div>
                    
                </section >
            )
        }
        else {
            return (
                <section className="container" style={{ backgroundColor: PcuBackgroundCode }} >
                    <div className="pagination-wrapper">
                        <div style={{ padding: "20px 0px 0px 0px", color: PcuBlueCode }}>
                            <h6>Query : ''</h6>
                        </div>
                        <Alert style={{ marginTop: '1rem', backgroundColor: PcuGreyCode, color: PcuBlueCode }}>
                            Cannot search on an empty field                                
                        </Alert>
                    </div>
                </section>
            );
        }
    }
}

export default Search;