import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import logoPCU from "../style/images/logo-no-bg.png"
import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink,
    InputGroup,
    InputGroupAddon,
    Button,
    Input
} from 'reactstrap';

const _navbar = { backgroundColor: '#00517c', padding: "0rem 0rem" };
const navlink = { color: '#ffffff', border: "0px" };
const pcuGreenColor = { color: '#8bc34a' }
const tabsStyle = { borderBottom: "1px solid #00517c" }
class Header extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            pageContext: {
                makeSearch: false,
                isOpen: false,
                searchText: "",
                request: {},
                refresh: false,
                activeTab: 'home'
            }
        };
        this.toggle = this.toggle.bind(this);
    }
    onSearchTextChange = (e) => {
        var nextSearchText = e.target.value;
        let nextPageContext = {
            ...this.state.pageContext, ...{
                searchText: nextSearchText
            }
        }
        this.setState({
            pageContext: nextPageContext
        })
    }

    handleSearchText = () => {
        var REQUEST = {
            type: "document",
            index: "documents",
            query: {
                query: {
                    match: {
                        title: this.state.pageContext.searchText
                    }
                },
                from: 0,
                size: 10
            }
        }

        let nextPageContext = {
            ...this.state.pageContext, ...{
                request: REQUEST,
                makeSearch: !this.state.pageContext.makeSearch,
                refresh: !this.state.pageContext.refresh,
                activeTab: 'search'
            }
        }
        this.setState({
            pageContext: nextPageContext
        });
        this.props.changeTab(nextPageContext);
    }

    toggle() {
        let nextPageContext = {
            ...this.state.pageContext, ...{
                isOpen: !this.state.pageContext.isOpen
            }
        }
        this.setState({
            pageContext: nextPageContext
        });
    }

    render() {
        return (
            <div>
                <Navbar style={_navbar} light expand="md">
                    <NavbarBrand style={pcuGreenColor} href="/">
                        <img src={logoPCU} alt="LOGO PCU" width="60" /> PCU
                    </NavbarBrand>
                    <NavbarToggler onClick={this.toggle} />
                    <Collapse isOpen={this.state.pageContext.isOpen} navbar>
                        <Nav className="ml-auto" tabs style={tabsStyle}>
                            <NavItem>
                                <InputGroup>
                                    <Input placeholder="Search... "
                                        onChange={(e) => this.onSearchTextChange(e)} ref="inputSearch"
                                        onKeyPress={e => { if (e.key === 'Enter') { this.handleSearchText() } }} />
                                    <InputGroupAddon addonType="append">
                                        <Button color="light" onClick={this.state.pageContext.searchText !== '' ? (this.handleSearchText) : (console.log(""))}> Search</Button>
                                    </InputGroupAddon>
                                </InputGroup>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="#" onClick={() => { this.props.changeTab({ ...this.state.pageContext.activeTab, activeTab: 'home' }); }}>Home</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="#" onClick={() => { this.state.pageContext.searchText === '' ? (this.props.changeTab({ ...this.state.pageContext.activeTab, activeTab: 'search' })) : (this.handleSearchText()) }}>Search</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="#" onClick={() => { this.props.changeTab({ ...this.state.pageContext.activeTab, activeTab: 'monitoring' }); }}>Monitoring</NavLink>
                            </NavItem>
                        </Nav>
                    </Collapse>
                </Navbar>
            </div>
        );
    }
}
export default Header