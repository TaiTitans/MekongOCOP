import axios from "axios"
import authService from "./auth.service";
const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
    },
    withCredentials: true 
});


api.interceptors.request.use(
    (config) => {
        const accessToken = authService.getAccessToken();
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

api.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
       if(error.response.status === 401){
    return authService.refreshTokens()
    .then(()=> {
        const config = error.config;
        config.headers.Authorization = `Bearer ${authService.getAccessToken()}`;
        return api.request(config);
    })
    .catch((refreshError)=>{
        console.error(refreshError);
        router.push({name: 'Login'});
        return Promise.reject(error);
    });
       }
       return Promise.reject(error);
    }
);

export default api;