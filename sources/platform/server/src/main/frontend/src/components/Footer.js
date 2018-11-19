import React from 'react';
import { Card, CardFooter } from 'reactstrap';
import mainLogo from '../style/images/logo-no-bg.png';
import { SocialIcon } from 'react-social-icons';
import 'bootstrap/dist/css/bootstrap.min.css';


class Footer extends React.Component {
    render() {
        return (
            <div>
                <Card>
                    <CardFooter className="text-muted">
                        <footer>
                            <div className="container-fluid">
                                <div className="row">
                                    <div className="hidden-xs hidden-sm col-md-3 col-lg-3">
                                        <nav>
                                            <a href="/" title="homepage" className="logo-container">
                                                <span className="footer-logo-circle">
                                                    <img alt="PCU logo" className="footer-logo-circle-img" src={mainLogo} width="128" height="128"/>
                                                </span>
                                            </a>
                                        </nav>
                                    </div>
                                    <div className="hidden-xs col-sm-6 col-md-2 col-lg-2">
                                        <nav>
                                            <h6>PCU</h6>
                                            <ul className="list-unstyled" >
                                                <li>
                                                    <a href="overview">Overview</a>
                                                </li>
                                                <li>
                                                    <a href="use_case">Use Cases</a>
                                                </li>
                                                <li>
                                                    <a href="resources">Resources</a>
                                                </li>
                                                <li>
                                                    <a href="news">News</a>
                                                </li>
                                                <li>
                                                    <a href="community">Community</a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                    <div className="hidden-xs col-sm-6 col-md-2 col-lg-2">
                                        <nav>
                                            <h6>Use Cases</h6>
                                            <ul className="list-unstyled">
                                                <li><a href="entreprise_search">Enterprise Search</a></li>
                                                <li><a href="e-commerce">E-Commerce</a></li>
                                                <li><a href="custommer_insight">Customer Insight</a></li>
                                            </ul>
                                        </nav>
                                    </div>
                                    <div className="hidden-xs hidden-sm col-md-2 col-lg-2">
                                        <nav>
                                            <h6>About</h6>
                                            <ul className="list-unstyled">
                                                <li><a href="brand_guideline">Brand Guideline</a></li>
                                                <li><a href="term_of_use">Terms of use</a></li>
                                            </ul>
                                        </nav>
                                    </div>
                                    <div className="col-xs-12 col-sm-12 col-md-3 col-lg-3">
                                        <nav id="socialNav">
                                            <h6 className="title">Follow Us</h6>
                                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://github.com/pcu-consortium" /></i>
                                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://twitter.com/PCUConsortium"/></i>
                                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://facebook.com/pcu-consortium" /></i>
                                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.linkedin.com/company/pcu-consortium" /></i>
                                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.slideshare.net/pcuconsortium" /></i>
                                            <p>&copy; 2017 PCU Consortium</p>
                                        </nav>
                                    </div>
                                </div>
                            </div>
                        </footer>
                    </CardFooter>
                </Card>
            </div>
        )
    }

}

export default Footer;