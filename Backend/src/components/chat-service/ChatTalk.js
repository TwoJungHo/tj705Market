import React from 'react'

function ChatTalk(props) {

  const isImageUrl = (url) => {
    return (url.match(/^http:\/\/localhost:8000\/imgfile-service\/getimgdata\?id=\d+$/) != null);
  };

 const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString();
  };

  return (
    <div>
    {isImageUrl(props.object.message) ? 
      <div>
        
        <span style={{fontSize: '5px'}}>{formatTimestamp(props.object.sendAt)}</span><br/><strong>{props.object.sender + " : "}</strong><br/>
        <img src={props.object.message} alt="Uploaded" style={{maxWidth: '200px', maxHeight: '200px'}}/>
      </div>
    :  
      <div>
      <div style={{fontSize: '5px'}}>
        {formatTimestamp(props.object.sendAt)}</div>
        <strong>{props.object.sender + " : "}</strong>
        {props.object.message}
      </div>
    }
    </div>
  )
}

export default ChatTalk