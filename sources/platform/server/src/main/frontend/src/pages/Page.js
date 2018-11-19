
import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Monitoring from '../components/Monitoring.js';
import Search from '../components/Search.js';


import {
    TabContent,
    TabPane
} from 'reactstrap';
import Home from '../components/Home.js';

class Page extends Component {
    constructor(props) {
        console.log("constructor Page");
        super(props);

        this.state = {
            pageContext: {
                activeTab: 'home'
            }
        };
    }

    componentWillReceiveProps(props) {
        console.log("componentWRP", props.pageContext)
        this.setState({
            pageContext: props.pageContext
        });
    }

    toggle() {
        // what to do
    }

    render() {
        //console.log("render page " + this.state.pageContext.activeTab)
        return (
            <main id="main-content">
                <TabContent activeTab={this.state.pageContext.activeTab}>
                    <TabPane tabId="home">
                        <Home />
                    </TabPane>
                    <TabPane tabId="search">
                        <Search pageContext={this.state.pageContext} />
                    </TabPane>
                    <TabPane tabId="monitoring">
                        <Monitoring />
                    </TabPane>
                </TabContent>
            </main>
        );
    }
}
export default Page
