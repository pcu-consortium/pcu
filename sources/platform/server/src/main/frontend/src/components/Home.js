
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/style.css';
import neurons from "../style/images/neurons.jpg"
import logoPCU from "../style/images/logo-no-bg.png"
import organization from "../style/images/organization-128.png"
import shoppingCart from "../style/images/shopping-cart-filled-128.png"
import speedometer from "../style/images/speedometer-128.png"
import lipn from "../style/images/lipn.jpg"
import idf from "../style/images/idf.png"
import esilv from "../style/images/esilv.jpg"
import armadillo from "../style/images/armadillo.png"
import bpi from "../style/images/bpi-france.png"
import wallix from "../style/images/wallix.png"
import smile from "../style/images/smile.png"


import {
    Card,
    CardText,
    CardTitle,
    CardImg,
    CardImgOverlay,
    Container,
    CardGroup,
    CardBody
} from 'reactstrap';

class Home extends React.Component {

    render() {
        return (
            <div>
                <div className="main_container">
                    <Card inverse>
                        <CardImg className="imgResponsive" src={neurons} alt="Card image cap" />
                        <CardImgOverlay style={{ backgroundColor: "rgba(0, 0, 0, 0.66)", textAlign: "center" }}>
                            <img src={logoPCU} alt="LOGO PCU" width="20%" />
                            <CardText className="cardText">Unified Knowledge Platform</CardText>
                            <CardText className="cardSmallText">Open Source Search framework for everyone.</CardText>
                        </CardImgOverlay>
                    </Card>
                </div>
                <Container fluid>
                    <div style={{ textAlign: "center", margin: "2% 0% 4% 0%", color: "#00517c", fontSize: "30pt" }}>
                        Use Cases
                        <h6 style={{ color: "#777777" }}>One search framework to rule them all.</h6>
                    </div>
                    <CardGroup style={{ marginLeft: "5%",marginRight: "5%" }} >
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "30%", marginLeft: "auto", marginRight: "auto"  }} src={organization} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>Enterprise Search</CardTitle>
                                <CardText>Centralise your search across your enterprise securely.</CardText>
                            </CardBody>
                        </Card>

                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "30%", marginLeft: "auto", marginRight: "auto" }} src={shoppingCart} alt="Card image cap" />
                            <CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>E-Commerce</CardTitle>
                                <CardText>Optimize search on your product catalog.</CardText>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "30%", marginLeft: "auto", marginRight: "auto"  }} src={speedometer} alt="Card image cap" />
                            <CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>Customer Insights</CardTitle>
                                <CardText>Check your customer satisfaction.</CardText>
                            </CardBody>
                        </Card>
                    </CardGroup>
                </Container>

                <Container fluid style={{ backgroundColor: "#eee" }}>
                    <div style={{ textAlign: "center", margin: "2% 0% 4% 0%", color: "#00517c", fontSize: "30pt" }}>
                        Partners
                        <h6 style={{ color: "#777777" }}>United we stand, divided we fall.</h6>
                    </div>
                    <CardGroup style={{ marginLeft: "5%",marginRight: "5%" }} >
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={bpi} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>BPI FRANCE</CardTitle>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={smile} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>SMILE</CardTitle>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={armadillo} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>ARMADILLO</CardTitle>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={lipn} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>LIPN</CardTitle>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={esilv} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>ESILV</CardTitle>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}  className="text-center">
                            <CardImg style={{ width: "15%", marginLeft: "auto", marginRight: "auto"  }} src={wallix} alt="Card image cap" />
                            < CardBody style={{ padding: "10px 0px" }}>
                                <CardTitle>WALLIX</CardTitle>
                            </CardBody>
                        </Card>
                    </CardGroup>
                    <div class="container-fluid">
                            <div class="col-lg-12 text-center pcu-margin-bottom">
                                <a href="http://www.smile.eu" target="_blank"><img alt="Smile" src={{smile}} /></a>
                                <a href="http://lipn.univ-paris13.fr" target="_blank"><img alt="LIPN" src="/assets/images/partners/lipn.jpg" /></a>
                                <a href="https://www.esilv.fr" target="_blank"><img alt="ESILV" src="/assets/images/partners/esilv.jpg" /></a>
                                <a href="https://www.proxem.com" target="_blank"><img alt="Proxem" src="/assets/images/partners/proxem.svg" /></a>
                                <a href="https://www.armadillo.fr" target="_blank"><img alt="Armadillo" src="/assets/images/partners/armadillo.png" /></a>
                                <a href="http://www.wallix.fr" target="_blank"><img alt="Wallix" src="/assets/images/partners/wallix.png" /></a>
                            </div>
                            <div class="col-lg-12 text-center">
                                <a href="http://www.bpifrance.fr" target="_blank"><img alt="BPI France" src="/assets/images/partners/bpi-france.png" /></a>
                                <a href="http://www.systematic-paris-region.org" target="_blank"><img alt="Systematic GTLL" src="/assets/images/partners/systematic.png" /></a>
                                <a href="https://www.iledefrance.fr" target="_blank"><img alt="Region Ile-de-France" src="/assets/images/partners/idf.png" /></a>
                            </div>
                        </div>
                </Container>
                        </div >
                        )
                    }
                }
                
export default Home;