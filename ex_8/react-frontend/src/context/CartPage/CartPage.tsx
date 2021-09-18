import React, {FC} from 'react';
import {CartPageStyled} from '../../styles/CartPageStyled';
import {Cart} from '../../components/Cart/Cart';
import { v4 as uuidv4 } from "uuid";
import {MainPage} from "../../components/MainPage/MainPage";

export const CartPage: FC = () => {

	return (
		<MainPage>
			<CartPageStyled key={uuidv4()}>
				<Cart/>
			</CartPageStyled>
		</MainPage>
	);
};
