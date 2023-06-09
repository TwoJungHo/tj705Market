import React from 'react'
import { Link } from 'react-router-dom'

function Navbar() {
  return (<>
    <div id='nav-fake-background'></div>
    <div id='navimg'>
      <Link to={"/"}>
        <img src="/img/negomain.png" style={{ width: '180px', height: 'auto'}} alt='' />
      </Link>
    </div>
    
    <div id='nav'>
      <Link className="link" to={"/board/insert"}>판매하기</Link>
      <Link className="link" to={"/selllist"}>우리동네 네고!</Link>
      <Link className="link" to={"/login"}>로그인</Link>
      <Link className="link" to={"/"}>글 목록</Link>
      <Link className="link" to={"/signup"}>회원가입</Link>
    </div>
  </>

  )
}

export default Navbar