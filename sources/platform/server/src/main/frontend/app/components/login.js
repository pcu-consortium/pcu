import React, {Component} from "react";

export default class LoginOverlay extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
       return (
            <div className={"loginOverlay" +  (this.props.isActiveOverlay ? ' show' : '')}>
                <button className="loginOverlay__btnBack btn--small green" onClick={this.props.toggleModal}>
                    <img src="/img/fleche.svg" alt="Back Arrow" width="26" height="14" />
                    Back
                </button>  
                <div className="loginOverlay__wrapperGlobal">
                    <h2 className="loginOverlay__title">Login</h2>
                    <div className="loginOverlay__wrapper">
                        <img src="/img/logo-big.png" alt="LOGO PCU" className="loginOverlay__logoPCU" width="206" height="206" />
                        <form className="loginOverlay__form">
                            <label for="">Email</label>
                            <input name="email" id="email" />
                            <label for="">Password</label>
                            <input name="password" id="password" />
                            <a href="" className="loginOverlay__forgotPassword">Forgot password?</a>
                            <input type="submit" className="btn--small green" value="LOG IN" />
                        </form>
                    </div>
                </div>
            </div>
       );
    }
 }
 