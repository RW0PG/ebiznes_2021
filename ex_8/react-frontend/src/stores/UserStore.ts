import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {getUser, SignInUser, signUpUser} from "../api/userService";
import Cookies from 'js-cookie';
import {UserDb} from "../interfaces/UserDb";


interface IUserStore {
	user?: UserDb
	password?: string
}

export class UserStore implements IUserStore {
	private rootStore: RootStore | undefined;
	user: UserDb | undefined;
	password: string | undefined;

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this);
		this.rootStore = rootStore;
	}

	getUser = async (userId: number) => {
		if (!this.user) {
			const user = await getUser(userId);
			console.log(user)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				SignInDetails: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
		}
	}

	signUp = async (email: string, password: string) => {
		if (!this.user) {
			const user = await signUpUser(email, password);
			Cookies.set('userId', user.data.id)
			console.log(user)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				SignInDetails: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
			this.password = password
		}
		return this.user;
	};

	signIn = async (email: string, password: string) => {
		if (!this.user) {
			const user = await SignInUser(email, password);
			Cookies.set('userId', user.data.id)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				SignInDetails: {
					providerId: user.data.providerID,
					providerKey: user.data.providerKey,
				}
			};
			this.password = password
		}
		return this.user;
	};

	clear = () => {
		this.user = undefined
		this.password = undefined
	}
}
