import React, {useState} from "react";
import MacAddressContextOverlay from "./details/MacAddressContextOverlay";
import ContextOverlayVisibilityWrapper from "../ContextOverlayVisibilityWrapper";
import Dot11MacAddressType from "./Dot11MacAddressType";

function MacAddress(props) {

  const address = props.addressWithContext ? props.addressWithContext.address : props.address;
  const context = props.addressWithContext ? props.addressWithContext.context : null;
  const oui = props.addressWithContext ? props.addressWithContext.oui : null;
  const isRandomized = props.addressWithContext ? props.addressWithContext.is_randomized : props.address.is_randomized;

  const href = props.href;
  const onClick = props.onClick;

  const type = props.type;

  const showOui = props.showOui;
  const highlighted = props.highlighted;

  const [overlayTimeout, setOverlayTimeout] = React.useState(null);
  const [overlayVisible, setOverlayVisible] = useState(false);

  const addressElement = () => {
    if (href || onClick) {
      return <a href={href ? href : "#"}
                onClick={onClick ? onClick : () => {}}
                className={highlighted ? "highlighted" : null}>{address}</a>
    } else {
      return <span style={{cursor: "help"}} className={highlighted ? "highlighted" : null}>{address}</span>;
    }
  }

  const contextElement = () => {
    if (!context) {
      return null;
    }

    return <i className="fa-solid fa-circle-info additional-context-available"
              title="Additional context available." />
  }

  const ouiElement = () => {
    if (!oui || !showOui) {
      return null;
    }

    return <span className="mac-address-oui">(Vendor: {oui ? oui : "Unknown"})</span>
  }

  const typeElement = () => {
    if (type) {
      return <Dot11MacAddressType type={type}/>
    }
  }

  const randomizedIcon = () => {
    if (!isRandomized) {
      return null;
    }

    return <i className="fa-solid fa-shuffle mac-address-randomized" title="Randomized MAC Address" />
  }

  const mouseOver = () => {
    setOverlayVisible(false);
    setOverlayTimeout(setTimeout(() => setOverlayVisible(true), 1000));
  }

  const mouseOut = () => {
    setOverlayVisible(false);
    if (overlayTimeout) {
      clearTimeout(overlayTimeout);
    }
  }

  return (
      <div onMouseEnter={mouseOver} onMouseLeave={mouseOut}>
        {typeElement()}{addressElement()}{randomizedIcon()}{contextElement()} {ouiElement()}

        <ContextOverlayVisibilityWrapper
            visible={overlayVisible}
            overlay={<MacAddressContextOverlay address={address} isRandomized={isRandomized} />} />
      </div>
  )

}

export default MacAddress;