import React, { Component } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

import { BrowserRouter as Router, Route } from "react-router-dom";

import Home from "./pages/Home.js";
import Header from "./components/Header.js";
import Footer from "./components/Footer.js";
import Monitoring from './components/Monitoring.js';


class App extends Component {


  render() {
    return (
      <Router>

        <div>

          <Header />
          <main id="main-content">
            <Route path="*" component={Home} />
            <Route path="/home" component={Home} />
            <Route exact path="/" component={Home} />
            <Route path="/monitoring" component={Monitoring} />

          </main>
          <Footer />
        </div>

      </Router>
    );
  }
}

export default App;
