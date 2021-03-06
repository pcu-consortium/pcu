import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Header from "./components/Header.js";
import Footer from "./components/Footer.js";
import Page from "./components/Page.js";
import { PcuBackgroundCode } from './components/Colors.js';

class App extends Component {
  constructor(props) {
    super(props);

    this.changeTab = this.changeTab.bind(this);
    this.state = {
      pageContext: this.props.pageContext
    };
  }

  changeTab(nextPageContext) {
    this.setState({
      pageContext: nextPageContext
    })
  }

  render() {
    return (
      <div style={{ backgroundColor: PcuBackgroundCode }}>
        <Header changeTab={(pageContext) => this.changeTab(pageContext)} />
        <Page pageContext={this.state.pageContext} />
        <Footer />
      </div>
    );
  }
}

export default App;
