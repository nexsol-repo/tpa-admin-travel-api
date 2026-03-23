15:03:39.250|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (1:BIGINT) <- [199]

15:03:39.252|DEBUG|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.SQL                       |select tipfe1_0.id,tipfe1_0.created_at,tipfe1_0.family_name,tipfe1_0.insurance_product_name,tipfe1_0.insurer_id,tipfe1_0.is_active,tipfe1_0.is_loss,tipfe1_0.sort_order,tipfe1_0.updated_at from travel_insurance_plan_family tipfe1_0 join travel_insurance_plan_family_map tipfme1_0 on tipfme1_0.family_id=tipfe1_0.id where tipfme1_0.plan_id=?

15:03:39.252|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (1:BIGINT) <- [8]

15:03:39.253|DEBUG|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.SQL                       |select tipfe1_0.id,tipfe1_0.created_at,tipfe1_0.family_name,tipfe1_0.insurance_product_name,tipfe1_0.insurer_id,tipfe1_0.is_active,tipfe1_0.is_loss,tipfe1_0.sort_order,tipfe1_0.updated_at from travel_insurance_plan_family tipfe1_0 where tipfe1_0.family_name=? and tipfe1_0.is_loss=? and tipfe1_0.is_active=true

15:03:39.253|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (1:VARCHAR) <- [가뿐한플랜B 실손제외]

15:03:39.253|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (2:BOOLEAN) <- [false]



.255|DEBUG|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.SQL                       |select tipe1_0.id,tipe1_0.age_group_id,tipe1_0.created_at,tipe1_0.insurer_id,tipe1_0.is_active,tipe1_0.plan_code,tipe1_0.plan_full_name,tipe1_0.plan_group_code,tipe1_0.plan_name,tipe1_0.insurance_product_name,tipe1_0.unit_product_code,tipe1_0.updated_at from travel_insurance_plan tipe1_0 where tipe1_0.plan_full_name like replace(concat(?,'%'),'\\','\\\\') and tipe1_0.age_group_id=? and tipe1_0.is_active=true

15:03:39.255|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (1:VARCHAR) <- [가뿐한플랜B 실손제외]

15:03:39.255|TRACE|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|org.hibernate.orm.jdbc.bind             |binding parameter (2:BIGINT) <- [2]

15:03:39.260| INFO|4d21f4d1911e1b428463d3475958de71,52af1f0161c767ed|c.n.t.c.a.c.ApiControllerAdvice         |CoreException : 플랜을 찾을 수 없습니다: 가뿐한플랜B 실손제외, 나이: 31

com.nexsol.tpa.core.error.CoreException: 플랜을 찾을 수 없습니다: 가뿐한플랜B 실손제외, 나이: 31

	at com.nexsol.tpa.core.domain.plan.PlanResolver.lambda$resolve$1(PlanResolver.java:47)