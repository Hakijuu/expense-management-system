import React, { Component } from 'react';
import DisplayUsersByName from './DisplayUsersByName';
import FindUsersToWalletService from '../services/FindUsersToWalletService';
import UserService from '../services/user.service';
class AddUsersToNewWalletComponent extends Component {


    constructor(props){

        super(props)
        this.state={
            
            usertoken: undefined,
            users: [],
            classNameHelper: ""

        }
    }


    componentDidMount() {
        const currentUser = UserService.getCurrentUser();
        if (currentUser) {

            
            this.setState({
                
                username: currentUser.login,
                usertoken: currentUser.token,
                
            });

           
        }
        
    }


    handleChange(e) {
        
        var {value} = e.target;
        if(value){
        

        FindUsersToWalletService.getUsers(value, this.state.usertoken).then((response)=>{
        this.setState({users: response.data})
        var users = this.state.users;
       
        console.log("WYSŁANO RZĄDANIE")
        
        

        })
       } 
    }

    setHelperClassname(user){
        var classNameHelper = this.state.classNameHelper;
        if(this.props.currentList.includes(user)){
            this.state.classNameHelper = "delete-user-from-list"
        }
        else{
            this.state.classNameHelper = "add-user-to-list"
          
        }
    }
    render() {
        return (
            <div>
                <label className="form-label"> Użytkownicy: </label>

                
                            <input
                                type="text"
                                className="form-control"
                                name="searchForUser"
                                placeholder="Znajdź użytkownika..."
                                required
                                onChange={(e) => {
                                    
                                    
                                    this.handleChange(e);
                                   // console.log("Aktualni użytkownicy do dodania: " + this.props.currentList)
                                    
                                }}
                            />

{
                         
                         this.state.users.map(
                             user =>
                             
                             <div key = {user.login} className = "center-content grid-container-3">
                               {
                                  this.setHelperClassname(user.login)
                               }

                                 <div>
                                     #ProfPic
                                 </div>
                                <div>
                                <label className = "form-label" htmlFor={user.login}>

                                        {user.login}
                                    
                                        
                                    </label>
                                </div>

                                <div>
                                    <input type="button"
                                        id={user.login} 
                                      
                                        className = { this.state.classNameHelper}
                                        name ="user" 
                                        value="Click" 
                                        
                                      
                                        onClick={e => {
                                            this.props.createUsersList(e);
                                           

                                            if ( !document.getElementById(user.login).classList.contains('add-user-to-list') ){
                                                
                                                document.getElementById(user.login).classList.add('add-user-to-list');
                                                document.getElementById(user.login).classList.remove('delete-user-from-list');
                                            }
                                            else {
                                                
                                                
                                                document.getElementById(user.login).classList.remove('add-user-to-list');
                                                document.getElementById(user.login).classList.add('delete-user-from-list');
                                            }
                                            
                                        }}
                                    >
                                            
                                    </input>
                                 </div>
                                 
                                  
                                 
                             </div>
                             
                         )   
             }
                          

                
            </div>
        );
    }
}

export default AddUsersToNewWalletComponent;