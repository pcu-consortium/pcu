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

const _navbar = { backgroundColor: '#00517c' };
const navlink = { color: '#ffffff' };
const pcuGreenColor = { color: '#8bc34a' }
class Header extends React.Component {
    constructor(props) {
        console.log("constructor Header");
        super(props);

        this.toggle = this.toggle.bind(this);
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
    }
    onSearchTextChange = (e) => {
        var nextSearchText = e.target.value;
        let nextPageContext = {
            ...this.state.pageContext, ...{
                searchText: nextSearchText
            }
        }
        this.state.pageContext =  nextPageContext;
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
                isOpen: !this.state.isOpen
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
                        <Nav className="ml-auto" tabs>
                            <NavItem>
                                <InputGroup>
                                    <Input placeholder="Search... "
                                        onChange={(e) => this.onSearchTextChange(e)} ref="inputSearch"
                                        onKeyPress={e => { if (e.key === 'Enter') { this.handleSearchText() } }} />
                                    <InputGroupAddon addonType="append">
                                        <Button color="light" onClick={this.handleSearchText}> Search</Button>
                                    </InputGroupAddon>
                                </InputGroup>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="#" onClick={() => { this.props.changeTab({ ...this.state.pageContext.activeTab, activeTab: 'home' }); }}>Home</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="#" onClick={() => { this.props.changeTab({ ...this.state.pageContext.activeTab, activeTab: 'search' }); }}>Search</NavLink>
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