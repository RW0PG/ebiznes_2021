import React, {FC, useState} from 'react';
import {ProductStyled} from './ProductStyled';
import {Card, CardActions, CardContent, CardMedia, IconButton, Tooltip} from '@material-ui/core';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

export interface ProductProps {
	id: number
	name: string
	price: number
	image: string
}

export const Product: FC<{store?: RootStore} & ProductProps> = inject("store")(observer(({
	store, id, name, price, image
},) => {

	const cartStore = store?.cartStore
	const [opened, setOpened] = useState<boolean>(false)

	const handleClick = () => {
		// tooltip management
		setOpened(true)
		setTimeout(() => {
			setOpened(false)
		}, 1000)

		cartStore?.addProduct({id: id, name: name, price: price, image: image})
	}

	return (
		<ProductStyled>
			<Card>
				<CardMedia className="pic"
					image={image}
					title={name}
				/>
				<CardContent>
					<h3>
						{name}
					</h3>
				</CardContent>
				<CardActions disableSpacing style={{height: '6vh'}}>
					<Tooltip title="Product added to basket!" open={opened} onClose={() => setOpened(false)}
					         disableFocusListener
					         disableHoverListener
					         disableTouchListener
					>
						<IconButton aria-label="add to cart" onClick={() => handleClick()}>
							<img height='40px' src='images/basket.png' alt='store'/>
						</IconButton>
					</Tooltip>
					<div className="price">{price} PLN</div>
				</CardActions>
			</Card>
		</ProductStyled>
	);
}));
