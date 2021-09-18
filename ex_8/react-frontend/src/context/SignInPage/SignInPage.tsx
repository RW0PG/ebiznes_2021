import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { MainPage } from '../../components/MainPage/MainPage';
import {SignIn} from "../../components/Auth/SignIn/SignIn";
import { LoginPageStyled } from '../../styles/LoginPageStyled';

export const SignInPage: FC = () => {

	return (
		<MainPage>
			<LoginPageStyled key={uuidv4()}>
				<SignIn/>
			</LoginPageStyled>
		</MainPage>
	);
};
