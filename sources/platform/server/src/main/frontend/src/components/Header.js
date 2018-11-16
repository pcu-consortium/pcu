import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import logoPCU from "../style/images/logo-no-bg.png"
import Search from "./Search.js";

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
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            makeSearch: false,
            isOpen: false,
            searchText: "",
            request: {},
            refresh: false,
        };
    }
    onSearchTextChange = (e) => {
        var searchText = e.target.value;
        console.log("query = " + searchText);
        this.setState((prevState) => ({ ...prevState, searchText: searchText }));
        console.log("onSearchTextChange", this.state, e);

    }

    handleSearchText = () => {
        console.log("handleSearchText query = " + this.state.searchText);
        var REQUEST = {
            type: "document",
            index: "documents",
            query: {
                query: {
                    match: {
                        title: this.state.searchText
                    }
                },
                from: 0,
                size: 10
            }
        }
        this.setState({
            request: REQUEST,
            makeSearch: !this.state.makeSearch,
            refresh: !this.state.refresh

        });
    }
    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
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
                    <Collapse isOpen={this.state.isOpen} navbar>
                        <Nav className="ml-auto" navbar>
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
                                <NavLink style={navlink} href="/home/">Home</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="/search/">Search</NavLink>
                            </NavItem>
                            <NavItem>
                                <NavLink style={navlink} href="/monitoring">Monitoring</NavLink>
                            </NavItem>

                        </Nav>
                    </Collapse>
                </Navbar>
                {this.state.makeSearch === true ?
                    (
                        <div>
                            <Search request={this.state.request} refresh={this.state.refresh} />
                        </div>
                    ) : (
                        ''
                    )}
            </div>
        );
    }
}
export default Header