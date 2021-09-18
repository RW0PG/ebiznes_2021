import {RootStore} from './RootStore';
import {makeAutoObservable} from 'mobx';
import {listOrderProducts, listOrders} from '../api/orderService';
import {getVoucherById} from '../api/voucherService';
import {getPayment} from '../api/paymentService';
import {listProductsByOrderId} from '../api/productService';
import {listStocks} from '../api/stockService';
import {listUserCreditCards} from '../api/creditCardService';
import {listUserAddresses} from '../api/UserCredsService';
import {OrderDb} from "../interfaces/OrderDb";
import {ProductDb} from "../interfaces/ProductDb";
import {ProductDetails} from "../interfaces/ProductDetails";
import {VoucherDb} from "../interfaces/VoucherDb";
import {PaymentDb} from "../interfaces/PaymentDb";
import {CreditCardDb} from "../interfaces/CreditCardDb";
import {UserAddressDb} from "../interfaces/UserAddressDb";

export interface IOrder {
	order: OrderDb
	voucher: VoucherDb
	payment: PaymentDb
	creditCard: CreditCardDb
	address: UserAddressDb
	products: any
}


interface IOrderStore {
	orders: IOrder[]
}

export class OrderStore implements IOrderStore {
	private rootStore: RootStore | undefined;

	orders: IOrder[] = [];
	loaded: boolean = false;

	constructor(rootStore?: RootStore) {
		makeAutoObservable(this);
		this.rootStore = rootStore;
	}

	refreshOrders = async (userId: number) => {

		let orderList;
		if (!this.loaded) {
			const orders = await listOrders(userId);
			orderList = orders.data
			console.log(orderList);
			this.loaded = true;
		} else {
			orderList = this.orders;
		}

		const orderProduct = await listOrderProducts();
		const stocks = await listStocks();
		const creditCards = await listUserCreditCards(userId);
		const userAddresses = await listUserAddresses(userId);
		this.orders = await Promise.all(orderList.map(async (order: OrderDb) => {
			let voucher;
			if (order.voucherId !== 0) {
				voucher = await getVoucherById(order.voucherId);
			} else {
				voucher = undefined
			}
			const payment = await getPayment(order.paymentId);
			const products = await listProductsByOrderId(order.id);
			const card = creditCards.data.filter((card: any) => payment.data.creditCardId === card.id);
			const address = userAddresses.data.filter((address: any) => order.addressId === address.id);
			const productsSummary = await Promise.all(products.data.map(async (product: ProductDb) => {
				const quantity = orderProduct.data.filter((orderProduct: any) => orderProduct.orderId === order.id && orderProduct.productId === product.id);
				const price = stocks.data.filter((stock: any) => product.stockId === stock.id);
				const productDetails: ProductDetails = {
					name: product.name,
					imageUrl: product.imageUrl,
					quantity: quantity.length > 0 ? quantity[0].amount : -1,
					price: price.length > 0 ? price[0].totalPrice : -1
				};
				return productDetails;
			}));
			const newOrder: IOrder = {
				order: order,
				voucher: voucher?.data,
				payment: {...payment.data, amount: payment.data.amount / 100},
				products: productsSummary,
				creditCard: card.length > 0 ? {
					...card[0],
					number: card[0].number.substr(card[0].number.length - 4, card[0].number.length)
				} : undefined,
				address: address.length > 0 ? address[0] : undefined
			};
			return newOrder;
		}));
		return this.orders;
	};

	listOrders = () => {
		return this.orders
	}

	clearOrders = () => {
		this.orders = [];
		this.loaded = false;
	};
}
