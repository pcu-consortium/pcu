import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { PcuBlueCode, PcuGreyCode } from './Colors.js';
import logoPCU from "../style/images/logo-no-bg-blue.png"
import { SocialIcon } from 'react-social-icons';
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

const _navbar = { backgroundColor: PcuGreyCode, padding: "0rem 0.3rem", borderColor: PcuBlueCode };
const navlink = { color: PcuBlueCode, border: "0px" };
const tabsStyle = { borderBottom: "1px solid " + PcuGreyCode }

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
                    <NavbarBrand style={{ color: PcuBlueCode }} href="/">
                        <img src={logoPCU} alt="LOGO PCU" width="60" /> PCU
                    </NavbarBrand>
                    <nav id="socialNav" className="d-none d-lg-block d-xl-block">
                        <h6><a style={{ color: PcuBlueCode }} href="https://pcu-consortium.github.io/">&copy; 2017 PCU Consortium</a></h6>
                        <i><SocialIcon style={{ height: 25, width: 25 }} url="https://github.com/pcu-consortium" /> </i>
                        <i><SocialIcon style={{ height: 25, width: 25 }} url="https://twitter.com/PCUConsortium" /> </i>
                        <i><SocialIcon style={{ height: 25, width: 25 }} url="https://facebook.com/pcu-consortium" /> </i>
                        <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.linkedin.com/company/pcu-consortium" /> </i>
                        <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.slideshare.net/pcuconsortium" /> </i>
                    </nav>
                    <NavbarToggler onClick={this.toggle} />
                    <Collapse isOpen={this.state.pageContext.isOpen} navbar>
                        <Nav className="ml-auto" tabs style={tabsStyle}>
                            <NavItem>
                                <InputGroup>
                                    <Input placeholder="Search... "
                                        onChange={(e) => this.onSearchTextChange(e)} ref="inputSearch"
                                        onKeyPress={e => { if (e.key === 'Enter') { this.handleSearchText() } }} />
                                    <InputGroupAddon addonType="append">
                                        <Button style={{ color: PcuGreyCode, backgroundColor: PcuBlueCode }} onClick={this.state.pageContext.searchText !== '' ? (this.handleSearchText) : (console.log(""))}> Search</Button>
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