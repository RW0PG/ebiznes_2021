import axios from "axios";


export const HOST = 'http://localhost:9000'


export const signUpUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/sign-up`, {
		email: email,
		password: password
	});
};

export const SignInUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/sign-in`, {
		email: email,
		password: password
	});
};

export const SignOutUser = async (email: string, password: string) => {
	return axios.post(`${HOST}/api/sign-out`, {
		email: email,
		password: password
	});
};

export const getUser = async (userId: number) => {
	return axios.get(`${HOST}/api/user/get-by-id/${userId}`);
};



