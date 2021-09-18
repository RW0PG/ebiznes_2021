import React, {FC} from 'react';
import {CartStyled } from '../../styles/CartStyled';
import {Button} from '@material-ui/core';
import {ItemInCart} from "./ItemInCart/ItemInCart";
import {inject, observer} from 'mobx-react';
import {RootStore} from '../../stores/RootStore';
import {v4 as uuidv4} from 'uuid';
import {useHistory} from 'react-router';

export const Cart: FC<{store?: RootStore}> = inject("store")(observer(({store}) => {
	const userStore = store?.userStore
	const cartStore = store?.cartStore
	const history = useHistory()

	const productsInCart = () => {
		return cartStore?.listProducts().map(product => {
			return (
				<ItemInCart
					key={uuidv4()} id={product.id} image={product.image} name={product.name} price={product.price}
					quantity={product.quantity}	amount={product.price * product.quantity}
				/>
			)
		});
	}

	const calculateTotal = () => {
		let total = 0
		cartStore?.listProducts().forEach(product => total += product.price * product.quantity)
		return total
	}

	const completePayment = () => {
		if (!userStore?.user) {
			history.push('/order')
			// history.push('/login')
		} else {
			history.push('/order')
		}
	}

	return (
		<CartStyled>
			<div className='mt-5 entries'>
				<div className='head'>
					<h4 className='section' style={{marginLeft: '19.5vw'}}>Product</h4>
					<h4 className='section'>Price</h4>
					<h4 className='section'>Quantity</h4>
				</div>
				{productsInCart()}
				<div className='p-4'>
					<h4 className='summary' style={{marginLeft: '51vw', display: 'inline'}}>Total: {calculateTotal().toFixed(2)} PLN</h4>
					<Button style={{height: '40px'}} variant="contained" color='primary' onClick={() => completePayment()}>
						Checkout
					</Button>
				</div>
			</div>
		</CartStyled>
	);
}));
