import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {ProductProps} from '../components/Product/Product';
import {listProducts} from '../api/product';
import {listStocks} from '../api/stock';
import {ProductDb} from "../interfaces/ProductDb";
import {StockDb} from "../interfaces/StockDb";


interface IProductStore {
	products: ProductProps[]
}

export class ProductStore implements IProductStore {
	private rootStore: RootStore | undefined;

	products: ProductProps[] = [];

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this)
		this.rootStore = rootStore;
	}

	listProducts = async () => {
		if (this.products.length === 0) {
			const stockList = await listStocks()
			const productList = await listProducts()
			console.log(productList)
			this.products = productList.data.map((product: ProductDb) => {
				const newProduct: ProductProps = {
					id: product.id,
					name: product.name,
					price: stockList.data.filter((stock: StockDb) => stock.id === product.stockId)[0].totalPrice,
					image: product.imageUrl
				}
				return newProduct
			})
		}
		return this.products
	}
}
