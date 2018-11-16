import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Header from "./components/Header.js";
import Footer from "./components/Footer.js";
import Page from "./pages/Page.js";

class App extends Component {
  constructor(props) {
    console.log("constructor App");
    super(props);

    this.changeTab = this.changeTab.bind(this);
    this.state = {
      pageContext: this.props.pageContext
    };
  }


  changeTab(nextPageContext) {
    console.log("changeTab", nextPageContext);
    this.setState({
      pageContext: nextPageContext
    })
  }

  render() {
    return (
        <div>

          <Header changeTab={(pageContext) => this.changeTab(pageContext)} />
          <Page pageContext={this.state.pageContext}/>
          <Footer />
        </div>
    );
  }
}

export default App;
