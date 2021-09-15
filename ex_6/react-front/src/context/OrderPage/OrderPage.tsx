import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {Page} from "../../components/Page/Page";
import {OrderPageStyled} from './OrderPageStyled';
import {UserAddressModal} from '../../components/UserAddress/UserAddress';
import {CreditCardModal} from '../../components/CreditCard/CreditCard';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {ImageStyled} from '../../components/Cart/CartItem/CartItemStyled';
import {Button, Col, Container, Row} from 'react-bootstrap';
import {DialogContentText, Input} from '@material-ui/core';
import {toJS} from 'mobx';
import Popup from 'reactjs-popup';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import AddIcon from '@material-ui/icons/Add';
import {UserAddressDb} from '../../interfaces/UserAddressDb';
import {CreditCardDb} from "../../interfaces/CreditCardDb";
import {VoucherDb} from "../../interfaces/VoucherDb";
import {getVoucherByCode} from '../../api/voucher';
import { createPayment } from '../../api/payment';
import {createOrder, createOrderProduct} from '../../api/order';
import {useHistory} from 'react-router';


const CreditCardPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton style={{color: '#000000'}}>
			<AddIcon style={{paddingRight: '10px'}}/>
			<Button variant="Primary" >Payment Credentials</Button>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<CreditCardModal close={close}/>
		)}
	</Popup>
);

const UserAddressPopupModal: FC = () => (

	<Popup closeOnDocumentClick={false} trigger={
		<IconButton style={{color: '#000000'}}>
			<AddIcon style={{paddingRight: '10px'}}/>
			<Button variant="button">Add address</Button>
		</IconButton>}
	       nested modal>
		{(close: any) => (
			<UserAddressModal close={close}/>
		)}
	</Popup>
);

export const OrderPage: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const cartStore = store?.cartStore;
	const userStore = store?.userStore;
	const orderStore = store?.orderStore;
	const addressStore = store?.addressStore;
	const creditCardStore = store?.creditCardsStore;
	const history = useHistory()

	const [code, setCode] = useState('')
	const [voucher, setVoucher] = useState<VoucherDb>()

	const [total, setTotal] = useState(0)
	const [prev, setPrev] = useState(total)

	const [addresses, setAddresses] = useState<UserAddressDb[]>();
	const [selectedAddress, setSelectedAddress] = useState<UserAddressDb>();

	const [creditCards, setCreditCards] = useState<CreditCardDb[]>();
	const [selectedCard, setSelectedCard] = useState<CreditCardDb>();

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				try {
					const result = await getVoucherByCode(code)
					console.log(result);
					setVoucher(result.data);
					if (result.data.amount > 0) {
						setPrev(total)
						setTotal(total - total * result.data.amount * 0.01)
					}
				} catch (e) {
					setVoucher(undefined)
					prev && setTotal(prev)
					console.log(e)
				}
			}
		})();
	}, [code]);

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const result = toJS(await addressStore?.listAddresses(userStore.user.id));
				console.log(result);
				setAddresses(result);
				result && setSelectedAddress(result[0]);
			}
		})();
	}, [addressStore?.addresses]);

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const result = toJS(await creditCardStore?.listCreditCards(userStore.user.id));
				setCreditCards(result);
				result && setSelectedCard(result[0]);
			}
		})();
	}, [creditCardStore?.card]);

	useEffect(() => {
		(async () => {
			let totalPrice = 0;
			cartStore?.listProducts().forEach(product => totalPrice += product.price * product.quantity);
			setTotal(totalPrice)
		})();
	}, [cartStore?.products]);

	const DeliveryAddress = () => {
		return addresses?.map((address: UserAddressDb) => {
			return (
				<Col id={uuidv4()} style={{
					cursor: 'pointer',
					borderRadius: '4px',
				}
				} onClick={() => setSelectedAddress(address)}>
					<div>{address.firstname} {address.lastname}</div>
					<div>{address.address}</div>
					<div>{address.zipcode} {address.city}</div>
					<div>{address.country}</div>
				</Col>
			);
		});
	};

	const CCPayment = () => {
		return creditCards?.map((card: CreditCardDb) => {
			return (
				<Col id={uuidv4()} style={{
					cursor: 'pointer',
					borderRadius: '4px',
				}
				} onClick={() => setSelectedCard(card)}>
					<div>{card.cardholderName}</div>
					<div>•••• •••• •••• {card.number}</div>
				</Col>
			);
		});
	};

	const summaryProduct = () => {
		return cartStore?.listProducts().map(product => {
			return (
				<Row>
					<div className="order-summary-item mt-2 mb-2" id={uuidv4()}>
						<ImageStyled height={'12vh'} width={'12vh'} image={product.image}/>
						<div className="m-4">Product name:  {product.name}</div>
						<div className="m-4">Amount:  {product.quantity}</div>
						<div className="m-4">Product  price: {product.price * product.quantity} PLN</div>
					</div>
				</Row>
			);
		});
	};

	const onSubmit = async () => {
		if (userStore?.user && selectedCard && selectedAddress) {
			try {
				const payment = await createPayment(userStore.user.id, selectedCard?.id, total);
				const order = await createOrder(userStore.user.id, selectedAddress.id, payment.data.id, voucher?.id)
				await cartStore?.products.forEach(async (product) => {
					await createOrderProduct(order.data.id, product.id, product.quantity);
				})
				console.log(order)
				orderStore?.clearOrders()
				orderStore?.refreshOrders(userStore.user.id)
				cartStore?.clearProducts()
				history.push('/history')
			}catch(error){
				console.log(error)
			}
		}
	};

	return (
		<Page>
			<OrderPageStyled key={uuidv4()}>
				<Container>
					<Row>
						<Col className="m-4">
							<div className="entries mt-4">
								<div className="order-container p-1">
									<Row className="text-center">
										{DeliveryAddress()}
									</Row>
									<Row className="text-center">
										<UserAddressPopupModal/>
									</Row>
									<Row className="text-center">
										{CCPayment()}
									</Row>
									<Row className="text-center">
										<CreditCardPopupModal/>
									</Row>
								</div>
							</div>
						</Col>

					</Row>
					<Col className="m-4">
						<div className="entries mt-5">
							<div className="text-center mb-3">
								<Typography variant="button">Products</Typography>
							</div>
							{summaryProduct()}
						</div>
					</Col>
					<Row>
						<div className="entries mt-4" style={{marginLeft: '1.7vw', maxWidth: '67vw'}}>
							<Row>
								<Col className='col-10'>
									<DialogContentText style={{color: '#000000',}}>Discount Code:</DialogContentText>
									<Input autoFocus value={code} type='input' onChange={(e) => {
										setCode(e.target.value)
									}}/>
								</Col>
								<Col className='col-2'>
									<Typography style={{fontWeight: 200, paddingBottom: 10}}>Total: {total} PLN</Typography>
									<Button style={{height: '40px'}} onClick={() => onSubmit()}>
										Submit Order
									</Button>
								</Col>
							</Row>
						</div>
					</Row>
				</Container>
			</OrderPageStyled>
		</Page>
	);
}));
