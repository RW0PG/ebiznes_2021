import React, {FC} from 'react';
import { Button } from 'react-bootstrap';
import {DialogContentText, Input} from '@material-ui/core';
import {useHistory} from 'react-router';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import { LoginStyled } from './LoginStyled';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';




export const Login: FC<{store?: RootStore}> = inject("store")(observer(({store}) => {
	const history = useHistory();
	const userStore = store?.userStore

	const onSubmit = async (data: {
		email: string,
		password: string,
	}, actions: FormikHelpers<any>) => {
		actions.resetForm();
		await userStore?.authorize()
		history.goBack()
	};

	return (
		<LoginStyled>
			<div className='login'>
				<Formik initialValues={{
					email: '',
					password: '',
				}}
				        onSubmit={(values, actions) => onSubmit(values, actions)}>
					{({errors, touched, values, isSubmitting}) => (

						<Form className="registerForm">
							<DialogContentText style={{height: 12, color: "#FCFDFE", fontWeight: 500, textAlign: "center"}}>Email:</DialogContentText>
							<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000", width: '100%'}} name='email'
							       required
							       error={errors.email && touched.email ? errors.email : null}/>

							<DialogContentText style={{height: 12, color: "#FCFDFE", fontWeight: 500, textAlign: "center"}}>Password:</DialogContentText>
							<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000", width: '100%'}} name='password'
							       type="password"
							       required
							       error={errors.password && touched.password ? errors.password : null}/>
							<div className="row">
								<Button type='submit' disabled={isSubmitting}
										variant="dark">Sign in</Button>
							</div>
							<div className="row">
								<Button onClick={() => history.push('/register')} disabled={isSubmitting}
								       variant="secondary">Sign up</Button>
							</div>
						</Form>
					)}
				</Formik>
			</div>
		</LoginStyled>
	);
}));

