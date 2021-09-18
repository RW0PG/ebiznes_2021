import React, {FC} from 'react';
import {StorePageStyled} from '../../styles/StorePageStyled';
import { ProductList } from '../../components/ProductList/ProductList';
import {v4 as uuidv4} from 'uuid';
import { MainPage } from '../../components/MainPage/MainPage';

export const StorePage: FC = () => {

	return (
		<MainPage>
			<StorePageStyled key={uuidv4()}>
				<ProductList/>
			</StorePageStyled>
		</MainPage>
	);
};
