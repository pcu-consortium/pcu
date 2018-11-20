
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/style.css';
import neurons from "../style/images/neurons.jpg"
import logoPCU from "../style/images/logo-no-bg.png"
import organization from "../style/images/organization-128.png"
import shoppingCart from "../style/images/shopping-cart-filled-128.png"
import speedometer from "../style/images/speedometer-128.png"


import {
    Card,
    CardText,
    CardTitle,
    CardImg,
    CardImgOverlay,
    Container,
    CardGroup,
    CardBody,
    Row
} from 'reactstrap';

class Home extends React.Component {

    render() {
        return (
            <div>
                <div style={{ backgroundColor: "rgba(0, 0, 0, 0.66)" }}>
                    <Card inverse style={{ border: "0px solid rgba(0, 0, 0, .125)" }} >
                        <CardImg width="100%" src={neurons} alt="Card image cap" />
                        <CardImgOverlay style={{ backgroundColor: "rgba(0, 0, 0, 0.66)", textAlign: "center" }}>
                            <img src={logoPCU} alt="LOGO PCU" width="12%" />
                            <CardText className="cardText">Unified Knowledge Platform</CardText>
                            <CardText className="cardSmallText">Open Source Search framework for everyone.</CardText>
                        </CardImgOverlay>
                    </Card>
                </div>
                <Container fluid style={{ lineHeight: '32px' }}>
                    <Row style={{ textAlign: "center" }}>
                        Use Cases
                    </Row>
                    <CardGroup >
                        <Card style={{ border: "0", marginLeft: "10%" }}>
                            <CardImg style={{ width: "30%" }} src={organization} alt="Card image cap" />
                            < CardBody >
                                <CardTitle>Enterprise Search</CardTitle>
                                <CardText>Centralise your search across your enterprise securely.</CardText>
                            </CardBody>
                        </Card>

                        <Card style={{ border: "0", marginLeft: "10%" }}>
                            <CardImg style={{ width: "30%" }} src={shoppingCart} alt="Card image cap" />
                            <CardBody>
                                <CardTitle>E-Commerce</CardTitle>
                                <CardText>Optimize search on your product catalog.</CardText>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}>
                            <CardImg style={{ width: "30%" }} src={speedometer} alt="Card image cap" />
                            <CardBody>
                                <CardTitle>Customer Insights</CardTitle>
                                <CardText>Check your customer satisfaction.</CardText>
                            </CardBody>
                        </Card>
                    </CardGroup>
                </Container>
            </div>
        )
    }
}

export default Home;