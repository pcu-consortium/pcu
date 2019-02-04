import React from 'react';
import { SocialIcon } from 'react-social-icons';
import 'bootstrap/dist/css/bootstrap.min.css';
import { PcuBlueCode } from './Colors.js';

class Footer extends React.Component {
    render() {
        return (
            <div className="container-fluid" style={{paddingTop:'1rem',paddingBottom:'1rem'}}>
                <div className="row">
                    <div className="d-lg-none d-block mx-auto" >
                        <nav id="socialNav">
                            <h6><a style={{ color: PcuBlueCode }} href="https://pcu-consortium.github.io/">&copy; 2017 PCU Consortium</a></h6>
                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://github.com/pcu-consortium" /> </i>
                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://twitter.com/PCUConsortium" /> </i>
                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://facebook.com/pcu-consortium" /> </i>
                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.linkedin.com/company/pcu-consortium" /> </i>
                            <i><SocialIcon style={{ height: 25, width: 25 }} url="https://www.slideshare.net/pcuconsortium" /> </i>
                        </nav>
                    </div>
                </div>
            </div>
        )
    }

}

export default Footer;