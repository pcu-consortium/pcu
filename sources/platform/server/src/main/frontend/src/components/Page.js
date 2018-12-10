
import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Monitoring from './Monitoring.js';
import Search from './Search.js';
import Home from './Home.js';

import {
    TabContent,
    TabPane
} from 'reactstrap';

class Page extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pageContext: {
                activeTab: 'home'
            }
        };
    }

    componentWillReceiveProps(props) {
        this.setState({
            pageContext: props.pageContext
        });
    }

    render() {
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
