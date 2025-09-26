# 🚨 Infrastructure Drift Detected

## Overview
Terraform found differences between your code and the actual infrastructure.  
This means something changed outside the usual Terraform workflow.

- **Environment**: <!-- filled by workflow -->
- **Date**: <!-- auto-set -->
- **Severity**: 🔴 Production / 🟡 Staging  
- **Logs**: [View Workflow Run](https://github.com/${{ github.repository }}/actions/runs/${{ github.run_id }})

---

## What Happened?
Drift can occur when:
- 🖱️ Manual changes in cloud console  
- 🤖 Auto-scaling events  
- 🗑️ Resources deleted outside Terraform  
- ⚙️ Other automation tools  
- 💥 Failed Terraform deploys  

---

## What To Do

1. **Review the Drift**
   - Check Terraform plan in the logs  
   - See which resources were added, changed, or destroyed  

2. **Decide Next Step**
   - ✅ If code is correct → run `terraform apply` to fix infra  
   - 📝 If manual changes are correct → update Terraform code, PR, then apply  
   - 🔎 Unsure → escalate and investigate (possible security issue)  

3. **Verify**
   - Re-run detection  
   - Test services  
   - Document root cause and resolution  

---

## Prevention
- 🔒 Limit manual infra changes  
- 📝 Require Terraform for all infra updates  
- 🔔 Add monitoring/alerts for drift  
- 📚 Educate team on best practices  

---

## Resolution Summary
- **Cause**: <!-- fill in -->  
- **Fix**: <!-- fill in -->  
- **Prevention**: <!-- fill in -->  

---

*This issue was auto-created by drift detection. Next run: Monday 06:00 UTC.*
