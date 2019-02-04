
import React from 'react';
import axios from 'axios';
import ReactJson from 'react-json-view';
import 'bootstrap/dist/css/bootstrap.min.css';
import { PcuBackgroundCode, PcuBlueCode } from './Colors.js';
import {
    Badge
} from 'reactstrap';

class Monitoring extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            status: 500,
            statusDetail: {}
        };
    }

    componentDidMount() {
        axios.get("/status").then(response => {
            console.log("get status " +response.data);
            this.setState({
                status: response.status,
                statusDetail: response.data
            });
            console.log("updated");
        }).catch(error => {
            this.setState({
                statusDetail: error.json()
            });
        });
    }
    render() {
        return (

            <section className="container" style={{ backgroundColor: PcuBackgroundCode, color: PcuBlueCode }} >
                <h3>Global status {this.state.status < 400 ? (<Badge color="success">OK</Badge>) : (<Badge color="error">ERROR</Badge>)}</h3>
                <h4> Status detail :</h4>
                {this.state.statusDetail !== '' ?
                (
                <div>  
                <h6> Kafka :</h6>
                <ReactJson src={this.state.statusDetail.pipeline} />
                <h6> Elasticsearch :</h6>
                <ReactJson src={this.state.statusDetail.index} />
                <h6> Storage :</h6>
                <ReactJson src={this.state.statusDetail.storage} />
                </div>
                )
                
                : ''
                }
            </section>

        )
    }
}

export default Monitoring;