import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {listUserAddresses} from '../api/UserCredsService';
import {UserAddressDb} from "../interfaces/UserAddressDb";

interface IAddressStore {
	addresses: UserAddressDb[]
}

export class AddressStore implements IAddressStore {
	private rootStore: RootStore | undefined;

	addresses: UserAddressDb[] = [];
	loaded: boolean = false

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}

	listAddresses = async (userId: number) => {
		if (!this.loaded) {
			const userAddresses = await listUserAddresses(userId)
			this.loaded = true
			this.addresses = userAddresses.data.map((address: UserAddressDb) => {
				const newAddress: UserAddressDb = {
					id: address.id,
					userId: address.userId,
					firstname: address.firstname,
					lastname: address.lastname,
					address: address.address,
					zipcode: address.zipcode,
					city: address.city,
					country: address.country
				}
				return newAddress
			})
		}
		return this.addresses
	}

	addAddress = (address: UserAddressDb) => {
		this.addresses = [...this.addresses, address]
	}
}
