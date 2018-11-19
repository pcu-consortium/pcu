
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import neurons from "../style/images/neurons.jpg"
import logoPCU from "../style/images/logo-no-bg.png"
import { Card, CardTitle, CardText, CardImg, CardImgOverlay } from 'reactstrap';
const HomeOverlay = {

}

class Home extends React.Component {

    render() {
        return (

            <div>
                <Card inverse>
                    <CardImg width="100%" src={neurons} alt="Card image cap" />
                    <CardImgOverlay>
                        <img src={logoPCU} alt="LOGO PCU" style={HomeOverlay} />
                        <CardTitle>PCU</CardTitle>
                        <CardText>PCU</CardText>
                        <CardText>
                            <small className="text-muted">Last updated 3 mins ago</small>
                        </CardText>
                    </CardImgOverlay>
                </Card>
            </div>

        )
    }
}

export default Home;