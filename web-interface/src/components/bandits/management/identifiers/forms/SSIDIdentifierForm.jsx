import React from 'react';
import Reflux from 'reflux';
import {compact} from "lodash/array";

class SSIDIdentifierForm extends Reflux.Component {

    constructor(props) {
        super(props);

        this.state = {
            ssids: [],
            errorMessage: "",
        };

        this._handleUpdate = this._handleUpdate.bind(this);
    }

    _handleUpdate(e) {
        const ssids = e.target.value.split(',');
        this.setState({ssids: ssids, errorMessage: ""});

        const explanation =  "a frame advertising any of the SSIDs ("+ compact(ssids) +") is recorded";

        this.props.configurationUpdate({
            configuration: {
                type: "SSID",
                ssids: compact(ssids)
            },
            explanation: explanation,
            ready: true
        });
    }

    render() {
        return (
            <form>
                <div className="form-group">
                    <label htmlFor="ssids">SSIDs</label>
                    <input type="text" className="form-control" id="ssids" placeholder="Enter the SSID (separate multiple SSIDs with a comma)"
                           value={this.state.ssids} onChange={this._handleUpdate} required />

                    <span className="text-danger">{this.state.errorMessage}</span>
                </div>
            </form>
        )
    }

}

export default SSIDIdentifierForm;