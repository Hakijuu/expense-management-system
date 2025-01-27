import React from 'react';
import Header from '../../components/Header';
import { Container, Row, Col} from 'reactstrap';
import UserService from '../../services/user.service';
import WalletDetailService from '../../services/WalletDetailService';
import { useState } from 'react';
import { useEffect } from 'react';
import { category } from '../../models/category';
import ExpenseService from '../../services/ExpenseService';
import CategoriesService from '../../services/CategoriesService';
import { Expense } from '../../models/expense';
import { ExpenseHolder } from '../../models/helpers/expenseHolder';
import ImageService from '../../services/ImageService';

function AddExpensePage () {
    var submitted = false;
    let walletID = '';
    if (sessionStorage && sessionStorage.getItem('walletID')) {
    walletID = JSON.parse(sessionStorage.getItem('walletID'));
    }
    const userToken = UserService.getCurrentUser()
    const [expenseCategories, getExpenseCategories] = useState([])
    const [walletUsers, setWalletUsers] = useState([])
    const [expenseName, setExpenseName] = useState("")
    const [expensePrice, setExpensePrice] = useState("")
    const [expenseCategory, setExpenseCategory] = useState([])
    const [expenseUsersList, setExpenseUsersList] = useState([])
    const [expenseReceiptImg, setExpenseReceiptImg] = useState()
    const [imagePreview, setImagePreview]=useState(null)
    
    const [errorMessage, setErrorMessage]=useState("")
    useEffect(()=>{
        const userData = UserService.getCurrentUser();
        
       
        WalletDetailService.getWalletDetail(walletID,userData.token)
        .then((response)=>{
            
            setWalletUsers(response.data.userList)
        })
        .catch(error=>{
            console.error({error})
        });
        
        CategoriesService.getCategories(userData.token)
            .then((response)=>{
                const allCategories = response.data
                getExpenseCategories(allCategories)
                const defaultId = Object.values(response.data)[0].id
                const defaultName = Object.values(response.data)[0].name
                setExpenseCategory(new category(defaultId,defaultName))

               
            })
            .catch(error=>{

                console.log({error})
               
            })
          
    },[])
    useEffect(()=>{
       
    },[expenseCategory])
    useEffect(()=>{
       
    },[imagePreview])

    function handleChangeName(e) {
        var {value} = e.target;
        setExpenseName(value)
        setErrorMessage("")
    }
    function handleChangePrice(e) {
        var {value} = e.target;
        setExpensePrice(value)
        setErrorMessage("")
    }

    function readExpenseCategory (e, categoryID){
        var {value} = e.target;
        
        setExpenseCategory(new category(categoryID,value)) 
        setErrorMessage("")
    }

    function handleCreateExpenseUsersList(e){
        let index
        const currentList = expenseUsersList
        if(e.target.checked){
            currentList.push(e.target.value)
            setErrorMessage("")
        }
        else {
            index = currentList.indexOf(e.target.value)
            currentList.splice(index, 1)
            if(currentList.length == 0) setErrorMessage("Lista musi zawierać co najmniej jednego użytkownika.")
          
        }
        setExpenseUsersList(currentList)
        
    }

    function handleImageChange(e){
        if (e.target.files && e.target.files[0]) {
            setImagePreview(URL.createObjectURL(e.target.files[0]))
            document.getElementById('img-preview-div').style.display = 'block';
            let img = e.target.files[0];
            var formData = new FormData();
            formData.append('image',e.target.files[0])
            console.log(formData)
            ImageService.uploadImg(formData, userToken.token)
            .then((response)=>{
                setExpenseReceiptImg(response.data)
            })
            .catch((error)=>{
                console.log(error)
                 setErrorMessage(error.response.data)
            })
           
           
          
          }
    }

    function handleClearChoosenImg(e){
        document.getElementById("insert-photo-button").value = "";
        setImagePreview(null)
        document.getElementById('img-preview-div').style.display = 'none';
    }
    function handleCreateExpense(e){
        e.preventDefault();
        submitted = true;
        if(expenseUsersList.length == 0){setErrorMessage("Lista musi zawierać co najmniej jednego użytkownika.")} 
        else{
        const expense = new Expense("",expenseName,expenseReceiptImg,expensePrice,expenseCategory)
        const expenseHolder = new ExpenseHolder(expense, expenseUsersList);
        ExpenseService.addExpense(walletID, expenseHolder, userToken.token)
        .then(()=>{
            window.location.href='/wallet' 
        })
        
        .catch(error=>{
            console.error({error})
            setErrorMessage(error.response.data)
        });
        window.location.href='/wallet'
        //console.log("ExpenseHolder:")
       // console.log(expenseHolder)
    }
        
    }
        return (
            <Container className="container">
                <Row>
                  <Header title="Dodaj wydatek"/>  
                </Row>
                <Col md="7" className="box-content base-text text-size form-container">
                    <div className = "href-text center-content">Dodaj wydatek</div>
                    <div className="separator-line"/>
                <form name="form"
                        method="post"
                        onSubmit={(e)=>handleCreateExpense(e)}
                        >
                        <div className={'form-group'}>
                            <label className="form-label"  htmlFor="Name">Nazwa: </label>
                            <input
                                required
                                type="text"
                                className="form-control"
                                name="name"
                                placeholder="Wpisz nazwę..."
                                minLength="1"
                                maxLength="45"
                                onChange={(e)=>handleChangeName(e)}
                            />
                            
                        </div>

                        <div className={'form-group'}>
                            <label className="form-label" htmlFor="price">Kwota: </label>
                            <input
                                required
                                type="number"
                                step="0.01"
                                className="form-control"
                                name="price"
                                placeholder="Wpisz kwotę..."
                                maxLength="1000"
                                pattern="^\d{0,8}(\.\d{1,2})?$"
                                onChange={(e)=>handleChangePrice(e)}
                            />
                            
                        </div>
                    <div className = "box-subcontent">
                        <div className="base-text text-size">
                        Kategoria:
                
                        </div>
                        
                        {
                         
                         expenseCategories.map(
                             category =>
                            
                             <div key = {category.id} className = "left-content custom-radiobuttons margin-left-text">
                           
                             <label className = "form-label text-size" htmlFor={category.id+"_category_id"}>
                               <input type="radio" id={category.id+"_category_id"} name="category" value={category.name} 
                                    defaultChecked = {category.name === Object.values(expenseCategories)[0].name}
                                   
                                   onChange={(e)=>readExpenseCategory(e, category.id)}
                                   >
                                       
                               </input>
                               
                               <div className="checkmark icons-size-2"></div>
                                {category.name}</label>
                               
                           </div> 
                                
                             
                         )   
             }
                    
                </div>    
                <div className = "box-subcontent">
                        <div className="base-text text-size">
                        Kogo dotyczy wydatek:
                
                        </div>
                        
                        {
                         
                         walletUsers.map(
                            user =>
                            
                             <div key = {user.userId} className = "left-content custom-checkboxes margin-left-text">
                           
                             <label className = "form-label text-size" htmlFor={user.userId}>
                               <input type="checkbox" id={user.userId} name="users" value={user.login} 
                                   
                                   
                                   onChange={(e)=>handleCreateExpenseUsersList(e)}
                                   >
                                       
                               </input>
                               
                               <div className="checkmark-checkbox icons-size-2"></div>
                                {user.login}</label>
                               
                           </div> 
                                
                             
                         )   
             }
                    
                </div>    
                <div className = "box-subcontent">
                        <div className="base-text text-size">
                        Zdjęcie paragonu:
                
                        </div>
                        
                        <input  
                            className="btn btn-primary btn-block form-button "
                            id = "insert-photo-button" 
                            type="file"
                            accept="image/png, image/jpeg, image/jpg" 
                            onChange={(e)=>handleImageChange(e)} />
                   <br /><br />
                   <div id="img-preview-div" style={{display:'none'}}>
                        <img src={imagePreview} className="img-preview" alt="Podgląd zdjęcia" /> 
                        <button
                        type="button"
                        className="delete-user-icon icons-size"
                        onClick={(e)=>handleClearChoosenImg(e)}
                        ></button>
                   </div>
              
                </div>    
                <div className="error-text text-size">
                    {errorMessage}
                </div>
                <br />
                        <button
                            className="btn btn-primary btn-block form-button text-size"
                            id = "mainbuttonstyle"
                            type = "submit"
                            onClick={e =>  {submitted = true
                               
                            }}
                            >
                            Dodaj
                        </button>
                </form>
                <br />
                  <button
                            className="btn btn-primary btn-block form-button text-size"
                            id = "mainbuttonstyle"
                            type = "button"
                            onClick={e =>  {
                              
                            }}
                            >
                            Anuluj
                        </button>
                </Col>
                
                
                                
            </Container>
        );
    
}

export default AddExpensePage;