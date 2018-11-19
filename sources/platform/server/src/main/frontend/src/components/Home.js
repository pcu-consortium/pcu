
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../style/style.css';
import neurons from "../style/images/neurons.jpg"
import logoPCU from "../style/images/logo-no-bg.png"
import organization from "../style/images/organization-128.png"
import shoppingCart from "../style/images/shopping-cart-filled-128.png"
import speedometer from "../style/images/speedometer-128.png"


import {
    Card, CardText, CardTitle, Button, CardImg, CardImgOverlay, Row, Col, Container, CardGroup, CardSubtitle, CardBody
} from 'reactstrap';
const HomeOverlay = {
    textAlign: "center",
    padding: "0rem"
}

class Home extends React.Component {

    render() {
        return (
            <div>
                <div style={{ backgroundColor: "rgba(0, 0, 0, 0.66)" }}>
                    <Card inverse >
                        <CardImg width="100%" src={neurons} alt="Card image cap" />
                        <CardImgOverlay style={{ backgroundColor: "rgba(0, 0, 0, 0.66)", textAlign: "center" }}>
                            <img src={logoPCU} alt="LOGO PCU" width="12%" />
                            <CardText className="cardText">Unified Knowledge Platform</CardText>
                            <CardText className="cardSmallText">Open Source Search framework for everyone.</CardText>
                        </CardImgOverlay>
                    </Card>
                </div>
                <Container fluid style={{ lineHeight: '32px' }}>
                    <CardGroup >
                        <Card style={{ border: "0" }}>
                            <CardImg style={{ width: "40%", borderRadius: "50%" }} width="10 % " src={organization} alt="Card image cap" />
                            < CardBody >
                                <CardTitle>Card title</CardTitle>
                                <CardSubtitle>Card subtitle</CardSubtitle>
                                <CardText>This is a wider card with supporting text below as a natural lead-in to additional content. This content is a little bit longer.</CardText>
                                <Button>Button</Button>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}>
                            <CardImg style={{ width: "40%" }} src={shoppingCart} alt="Card image cap" />
                            <CardBody>
                                <CardTitle>Card title</CardTitle>
                                <CardSubtitle>Card subtitle</CardSubtitle>
                                <CardText>This card has supporting text below as a natural lead-in to additional content.</CardText>
                                <Button>Button</Button>
                            </CardBody>
                        </Card>
                        <Card style={{ border: "0" }}>
                            <CardImg style={{ width: "40%" }} src={speedometer} alt="Card image cap" />
                            <CardBody>
                                <CardTitle>Card title</CardTitle>
                                <CardSubtitle>Card subtitle</CardSubtitle>
                                <CardText>This is a wider card with supporting text below as a natural lead-in to additional content. This card has even longer content than the first to show that equal height action.</CardText>
                                <Button>Button</Button>
                            </CardBody>
                        </Card>
                    </CardGroup>
                </Container>
            </div>
        )
    }
}

export default Home;