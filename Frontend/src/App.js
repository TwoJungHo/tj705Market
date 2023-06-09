import { BrowserRouter, Route, Routes, useLocation } from 'react-router-dom';
import './App.css';
import ChatRoom from './components/chat-service/ChatRoom';
import Login from './components/auth/Login';
import MemberInsert from './components/user-service/MemberInsert';
import RateTest from './RateTest';
import BoardInsert from './components/board-service/BoardInsert';
import Navbar from './components/Navbar';
import BoardUpdate from './components/board-service/BoardUpdate';
import BoardDetail from './components/board-service/BoardDetail';
import SellRangedList from './components/sell-service/SellRangedList';
import ReviewInsert from './components/review-service/ReviewInsert';

function App() {

function HeaderController() {
  const location = useLocation();
  const isChatRoomPage = location.pathname.startsWith("/chatwith/");
  
  return (
    <header>{isChatRoomPage ? '' : <Navbar/>}</header>
  )
}


  return (
    <div>
      
      <BrowserRouter>
      <HeaderController/>
      
      <div id='bodybox'>
      <div id='router_container'>
      <Routes>
        <Route path="/login" Component={Login}/>
        <Route path="/signup" Component={MemberInsert}/>

        <Route path="/RateTest" Component={RateTest}/>
        <Route path="/selllist" Component={SellRangedList}/>
        <Route path="/board/insert" Component={BoardInsert}/>
        <Route path="/board/update/:sellId" Component={BoardUpdate}/>
        <Route path="/board/detail/:sellId" Component={BoardDetail}/>
        
        <Route path="/chatwith/:username" Component={ChatRoom}/>

        <Route path="/review/:sellId" Component={ReviewInsert}/>
        
      </Routes>
      </div>
      </div>

      </BrowserRouter>
    </div>
    
  );
}

export default App;
