import React, { useRef, useState } from 'react'
import SellInsert from '../sell-service/SellInsert'
import KakaoMap from '../sell-service/kakaoMapComponents/KakaoMap';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import { fetch_multiForm } from '../../networkFns/fetchFns';
import KakaoMapBoardInsert from '../sell-service/kakaoMapComponents/KakaoMapBoardInsert';

const modules = {
  toolbar: {
    container: [
      ['bold', 'italic', 'underline', 'strike'],
      ['link', 'image'],
    ],
    handlers: {},
  },
};

function BoardInsert() {

  const editor = useRef();
  const title = useRef();
  const [imgFile, setImgFile] = useState();
  const [productName, setProductName] = useState();
  const [price, setPrice] = useState();
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();
  const [sellState, setSellState] = useState();


  const setPositionData = { setLatitude, setLongitude }
  const setSellData = { setImgFile, setProductName, setPrice, setSellState }







  function saveButtonClickHandler() {
    const formData = new FormData();
    const htmlString = editor.current.getEditor().root.innerHTML;
    const deltaString = JSON.stringify(editor.current.getEditor().getContents());
    const delta = JSON.parse(deltaString);
    console.log(editor.current.getEditor().getContents());
    console.log(delta);
    formData.append("title", title.current.value);
    formData.append("imgFile", imgFile);
    formData.append("productName", productName);
    formData.append("price", price);
    formData.append("latitude", latitude);
    formData.append("longitude", longitude);
    formData.append("sellState", sellState);
    formData.append("htmlString", htmlString);
    //formData.append("deltaString", deltaString);


    console.log(formData.get("title"));
    console.log(formData.get("imgFile"));
    console.log(formData.get("productName"));
    console.log(formData.get("price"));
    console.log(formData.get("latitude"));
    console.log(formData.get("longitude"));
    console.log(formData.get("sellState"));
    console.log(formData.get("htmlString"));

    fetch_multiForm("POST", "http://localhost:8000/board-service/boards", formData)

  }



  const uploadImgCallBack = async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('filename', file.name);

    try {
      const response = await fetch('http://localhost:8000/imgfile-service/uploadimg', {
        method: 'POST',
        body: formData,
      });
      const data = await response.json();
      console.log(data.result)
      return `http://localhost:8000/imgfile-service/getimgdata?id=${data.result}`;
    } catch (error) {
      console.log(error);
    }
  };

  const imageHandler = () => {
    const quillEditor = editor.current.getEditor();
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
      const file = input.files[0];
      const url = await uploadImgCallBack(file);
      const range = quillEditor.getSelection(true);
      quillEditor.insertEmbed(range.index, 'image', url);
    };
  };
  modules.toolbar.handlers = { image: imageHandler }







  return (
    <div className='routes'>
      <div style={{width: '1080px', textAlign: 'center'}}>
      <input id='board-insert-title' placeholder='제목' ref={title}></input>
      <button className = 'btn-lg' onClick={saveButtonClickHandler}>등록</button>
      </div>
        
      <div style={{width: '1080px'}}>
        <SellInsert setSellData={setSellData} />
      </div>

      <div>
        <KakaoMapBoardInsert setPositionData={setPositionData} />
      </div>

      <div>
        <ReactQuill
          ref={editor}
          theme="snow"
          modules={modules}
          id='board-insert-quill'
        />
      </div>

    </div>
  )
}

export default BoardInsert