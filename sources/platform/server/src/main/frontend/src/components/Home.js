
import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { PcuBackgroundCode, PcuBlueCode } from './Colors.js';
import '../style/style.css';
import logoPCU from "../style/images/logo-no-bg-blue.png";

import {
    Card,
    CardText
} from 'reactstrap';

class Home extends React.Component {

    render() {
        return (
            <section className="container mx-auto" style={{backgroundColor:PcuBackgroundCode}}>
                <div className="main_container" style={{backgroundColor:PcuBackgroundCode}}>
                    <Card style={{backgroundColor:PcuBackgroundCode}}>
                        <img src={logoPCU} alt="LOGO PCU" className="mx-auto d-none d-xl-block d-lg-block d-md-block"/>
                        <img src={logoPCU} alt="LOGO PCU" width="100%" className="mx-auto d-xl-none d-lg-none d-md-none"/>
                        <CardText className="cardText mx-auto" style={{ color: PcuBlueCode }}>PCU</CardText>
                        <CardText className="cardText mx-auto" style={{ color: PcuBlueCode }}>Unified Knowledge Platform</CardText>
                        <CardText className="cardSmallText mx-auto" style={{ color: PcuBlueCode }}>Open Source Search framework for everyone.</CardText>
                    </Card>
                </div>

            </section>
        )
    }
}

export default Home;