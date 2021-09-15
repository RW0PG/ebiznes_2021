import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {getUser, login, register} from '../api/user';
import {UserDb} from "../interfaces/UserDb";
import Cookies from 'js-cookie';



interface IUserStore {
	user?: UserDb
	password?: String
}

export class UserStore implements IUserStore {
	private rootStore: RootStore | undefined;
	user: UserDb | undefined
	password: string | undefined

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}

	authorize = async (userId: number) => {
		if (!this.user) {
			const user = await getUser(userId)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				nickname: user.data.email,
				password: user.data.password,
			}
		}
		return this.user
	}

	register = async (email: string, nickname: string, password: string) => {
		if (!this.user) {
			const user = await register(email, nickname, password);
			Cookies.set('userId', user.data.id)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				nickname: user.data.nickname,
				password: user.data.password
			};
		}
		return this.user;
	};

	login = async (email: string, password: string) => {
		if (!this.user) {
			const user = await login(email, password);
			Cookies.set('userId', user.data.id)
			this.user = {
				id: user.data.id,
				email: user.data.email,
				password: user.data.password
			};
		}
		return this.user;
	};

	clear = () => {
		this.user = undefined
		this.password = undefined
	}
}
