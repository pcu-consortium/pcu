
import React, { Component } from 'react';
import Monitoring from '../components/Monitoring.js';
import Search from '../components/Search.js';


import {
    TabContent,
    TabPane
} from 'reactstrap';

class Page extends Component {
    constructor(props) {
        console.log("constructor Page");
        super(props);

        this.state = {
            pageContext: {
                activeTab:'home'
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
                        <div>Home</div>
                    </TabPane>
                    <TabPane tabId="search">
                        <Search pageContext={this.state.pageContext}/>
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
