import React from "react";
import { fetchFn } from "../../NetworkUtils";


function Login() {
  localStorage.setItem("jwt", null);

  function onSubmitHandler(e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const username = formData.get("username");
    const password = formData.get("password");
    const dto = { username, password };

    fetchFn("POST", "http://localhost:8000/user-service/login", dto)
    .then(
      (data) => {
        console.log(data)
        localStorage.setItem("jwt", data.result.token);
        localStorage.setItem("longitude", data.result.longitude);
        localStorage.setItem("latitude", data.result.latitude);
        window.location.href = "/board/insert";
      }
    );
  }

  return (
    <div>
      <form action="#" onSubmit={onSubmitHandler}>
        <div className="topmargin" style={{display: 'inline-block'}}>
        <input style={{width: '445px', height: '33px', margin: '1%'}} placeholder="ID" className="border" name="username" /> <br />
        <input style={{width: '445px', height: '33px', margin: '1%'}} placeholder="PW" className="border" type="password" name="password" /></div><button className="loginbutton" style={{display: 'inline-block', verticalAlign: '-12px' }}></button>
        
      </form>
    </div>
  );
}

export default Login;
