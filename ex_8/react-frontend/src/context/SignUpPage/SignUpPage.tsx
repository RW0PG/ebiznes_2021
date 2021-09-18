import React, {FC} from 'react';
import {v4 as uuidv4} from 'uuid';
import { MainPage } from '../../components/MainPage/MainPage';
import { SignUp} from "../../components/Auth/SignUp/SignUp";
import { RegisterPageStyled } from '../../styles/RegisterPageStyled';

export const SignUpPage: FC = () => {

	return (
		<MainPage>
			<RegisterPageStyled key={uuidv4()}>
				<SignUp/>
			</RegisterPageStyled>
		</MainPage>
	);
};
