import axios from "axios";
const WALLET_CATEGORY_API_BASE_URL = "http://localhost:8080/categories"

class CategoryService {

    getCategories(){
        return axios.get(WALLET_CATEGORY_API_BASE_URL);
    }

}

export default new CategoryService();