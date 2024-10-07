import { jwtDecode } from 'jwt-decode';


function getAccessToken() {
    const name = "accessToken=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i];
      while (c.charAt(0) == ' ') {
        c = c.substring(1);
      }
      if (c.indexOf(name) == 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";
  }
  
  function getRefreshToken() {
    const cookies = document.cookie.split(";");
    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i].trim();
      if (cookie.startsWith("refreshToken=")) {
        return cookie.substring("refreshToken=".length, cookie.length);
      }
    }
    return null;
  }
  
  function refreshTokens() {
    const refreshToken = getRefreshToken();
  
    if (refreshToken) {
      return api.post("/api/v1/user/refresh-token", { refreshToken })
        .then((response) => {
          const newAccessToken = response.data;
          document.cookie = `accessToken=${newAccessToken}; path=/; max-age=3600; secure;`;
          return { newAccessToken };
        })
        .catch((error) => {
          console.error(error);
          throw error;
        });
    } else {
      return Promise.reject(new Error("No refresh token found."));
    }
  }

  // Phương thức để kiểm tra thời hạn của token
function isTokenValid(token) {
    if (!token) return false;
    try {
      const decoded = jwtDecode(token);
      const currentTime = Date.now() / 1000; // Lấy thời gian hiện tại tính bằng giây
      return decoded.exp > currentTime; // So sánh thời gian hết hạn với thời gian hiện tại
    } catch (error) {
      return false; // Trả về false nếu token không hợp lệ hoặc không thể giải mã
    }
  }
  function getUserRoles() {
    const token = getAccessToken();
    console.log("Token to decode:", token);
    if (!token) return [];
    try {
      const decoded = jwtDecode(token);
      console.log("Decoded token:", decoded);
      return decoded.roles || [];
    } catch (error) {
      console.error("Error decoding token:", error);
      return [];
    }
  }
  export default {
    getAccessToken,
    getRefreshToken,
    refreshTokens,
    isTokenValid,
    getUserRoles
  };