
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
                        </div >
                        )
                    }
                }
                
export default Home;