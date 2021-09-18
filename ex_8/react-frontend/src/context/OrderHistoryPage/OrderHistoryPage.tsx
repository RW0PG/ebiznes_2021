import React, {FC, useEffect, useState} from 'react';
import {v4 as uuidv4} from 'uuid';
import {MainPage} from "../../components/MainPage/MainPage";
import {Entry, OrderHistoryPageStyled} from '../../styles/OrderHistoryPageStyled';
import {RootStore} from "../../stores/RootStore";
import {inject, observer} from 'mobx-react';
import {toJS} from 'mobx';
import {ProductDetails} from "../../interfaces/ProductDetails";
import {IOrder} from "../../stores/OrdersStore";
import {Col, Row} from 'react-bootstrap';
import {ImageStyled} from "../../styles/ItemInCardStyled";
import {Typography} from '@material-ui/core';

export const OrderHistoryPage: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const userStore = store?.userStore;
	const ordersStore = store?.orderStore;

	const [orders, setOrders] = useState<IOrder[]>();

	useEffect(() => {
		if (userStore?.user) {
			ordersStore?.refreshOrders(userStore.user.id)
		}
	}, [])

	useEffect(() => {
		(async () => {
			if (userStore?.user) {
				const result = toJS(await ordersStore?.listOrders());
				console.log(result);
				if (result && result?.length > 0) {
					setOrders(result);
				}
			}
		})();
	}, [ordersStore?.orders]);

	const prepareOrderSummary = () => {
		return orders?.map((order, index) => {
			return (

				<Entry>
					<Row>
						<Typography>Order #{index + 1}</Typography>
						<Col>
							{prepareProductsSummary(order.products)}
						</Col>
						<Col>
							<Row>
								<div className="text-center mt-3 mb-3">
									<Typography variant="button">Delivery Address</Typography>
									<div>{order.address.firstname} {order.address.lastname}</div>
									<div>{order.address.address}</div>
									<div>{order.address.zipcode} {order.address.city}</div>
									<div className='mb-2'>{order.address.country}</div>
								</div>
							</Row>
							<Row>
								<div className="text-center mb-3 mt-1">
									<Typography variant="button">Card details</Typography>
									<div>{order.creditCard.cardholderName}</div>
									<div className='mb-2'>xxxx xxxx xxxx {order.creditCard.number}</div>
								</div>
							</Row>
							{
								order.voucher &&
								<Row>
									<div className="text-center mb-3">
										<Typography variant="button">Voucher</Typography>
										<div className='mb-2'>{order.voucher.code}: -{order.voucher.amount}%</div>
									</div>
								</Row>
							}
							<Row>
								<div className="text-center mb-3">
									<Typography variant="button">Total</Typography>
									<div className='mb-2'>{order.payment.amount} PLN</div>
								</div>
							</Row>
						</Col>
					</Row>
				</Entry>
			);
		});
	};

	const prepareProductsSummary = (products: ProductDetails[]) => {
		return products?.map((product: ProductDetails) => {
			return (
				<Row>
					<div className="order-summary-item mt-2 mb-2" id={uuidv4()}>
						<ImageStyled height={'8vh'} width={'12vh'} image={product.imageUrl}/>
						<div className="m-4">{product.name}</div>
						<div className="m-4">quantity: {product.quantity}</div>
						<div className="m-4">{product.price * product.quantity} PLN</div>
					</div>
				</Row>
			);
		});
	};

	return (
		<MainPage>
			<OrderHistoryPageStyled key={uuidv4()}>
				<div className="entries">
					{prepareOrderSummary()}
				</div>
			</OrderHistoryPageStyled>
		</MainPage>
	);
}));
